// import { LOCATION_CHANGE } from 'react-router-redux';

import { eventChannel } from 'redux-saga';
import {
  put,
  take,
  call,
  fork,
  select,
  actionChannel
} from 'redux-saga/effects';
import WS_ACTIONS, {
  _websocketMessage,
  _websocketOpen
} from "./redux-websocket-middleware/actions";
import { blynkWsLogin } from "./blynk-websocket-middleware/actions";
import { browserHistory } from "react-router";
import { ACTIONS } from './redux-websocket-middleware/actions';
// import { Handlers } from "./blynk-websocket-middleware/handlers";
// import {
//   API_COMMANDS,
//   COMMANDS,
//   RESPONSE_CODES
// } from "./blynk-websocket-middleware/commands";

// import {
//   getCommandKeyName,
// } from "./selectors";

export const INIT_ACTION_TYPE = 'CONNECT';

// let MSG_ID = 0;
let socket_reconnect_retry = 0;
const MAX_SOCKET_RETRY = 5000;

/* eslint-disable no-constant-condition */

/* eslint-disable require-yield */
let socket;
let options;

function* connect() {
  const state = yield select();
  return new Promise((resolve, reject) => {
    try {
      socket = new WebSocket(options.endpoint);
      socket.binaryType = 'arraybuffer';
      socket.addEventListener('open', () => {
        put(_websocketOpen());
        if (state && state.Account && state.Account.credentials && state.Account.credentials.username) {
          const { username, password } = state.Account.credentials;
          console.log(1);
          put(blynkWsLogin({
            username,
            hash: password
          }));
        } else {
          console.log(2);
          browserHistory.push('/login');
        }
        resolve(socket);
      });
      socket.addEventListener('error', (err) => {
        console.error(err);
        reject(err);
      });
    } catch (err) {
      console.error(err);
      reject(err);
    }
  });
}

function socketSubscribe(emitter) {
  socket.addEventListener('message', socket_message => {
    console.log('BBBBBBBBBBBBBBBBBB', socket_message);

    emitter(_websocketMessage(socket_message));
  });

  socket.addEventListener('close', () => {
    reconnect().then(() => {
      socketSubscribe(emitter);
    });
  });
}

function reconnect() {
  return new Promise((resolve, reject) => {
    retry(resolve, reject).then((sock) => {
      resolve(sock);
    });
  });
}

function retry(resolve, reject) {
  return new Promise(() => {
    setTimeout(() => {
      connect().then((sock) => {
        socket_reconnect_retry = 0;
        resolve(sock);
      }).catch((err) => {
        if (socket_reconnect_retry < MAX_SOCKET_RETRY) {
          socket_reconnect_retry += 1;
          retry(resolve, reject);
        } else {
          reject(err);
        }
      });
    }, 5000);
  });
}

function* subscribeToSocketEventChannel() {
  return eventChannel(emitter => {
    socketSubscribe(emitter);

    return () => {};
  });
}

function* handleInput() {
  const socketChan = yield actionChannel(WS_ACTIONS.WEBSOCKET_SEND);
  const value = yield take(ACTIONS.WEBSOCKET_CONNECT);
  console.log(value);
  while (true) {
    const action = yield take(socketChan);
    console.log('AAAAAAAAAAAAAAAAAAAAAAAAAAAA', action);

    // if (action.type === WS_ACTIONS.WEBSOCKET_SEND) {
    if (!socket)
      throw new Error(`Cannot write WS. Socket doesn't exists`);

    if (options.isDebugMode) {
      options.debug('webSocketSend', action.value);
    }

    socket.send(action.value);
    // }
  }
}

function* handleOutput() {
  const socketEventChannel = yield call(subscribeToSocketEventChannel, socket);
  while (true) {
    const message = yield take(socketEventChannel);
    yield put(message);
  }
}

function* handleSocket() {
  yield fork(handleOutput);
  yield fork(handleInput);
}

export function* blynkSaga() {
  while (true) {
    options = yield take(INIT_ACTION_TYPE);

    if (options.isDebugMode && !options.debug) {
      options.debug = function () {
        /* eslint-disable */
        console.log.apply(null, ['WebSocket Debug:', ...arguments]);
        /* eslint-enable */
      };
    }

    yield call(connect);
    yield fork(handleSocket);
  }
}
