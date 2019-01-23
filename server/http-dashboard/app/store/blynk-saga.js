import { eventChannel } from 'redux-saga';
import {
  put,
  take,
  call,
  fork,
  select,
  actionChannel,
} from 'redux-saga/effects';
import {
  ACTIONS, blynkWsLogin, _websocketMessage,
  _websocketOpen
} from "./blynk-websocket-middleware/actions";
import { browserHistory } from "react-router";

export const INIT_ACTION_TYPE = 'CONNECT';

let socket_reconnect_retry = 0;
const MAX_SOCKET_RETRY = 5000;

/* eslint-disable no-constant-condition */

/* eslint-disable require-yield */
let socket;
let options;

function* connect() {
  const state = yield select();
  return socketConnect(state);
}

function socketConnect(state) {
  return new Promise((resolve, reject) => {
    try {
      socket = new WebSocket(options.endpoint);
      socket.binaryType = 'arraybuffer';
      socket.addEventListener('open', () => {
        put(_websocketOpen());

        if (state && state.Account && state.Account.credentials && state.Account.credentials.username) {
          const { username, password } = state.Account.credentials;
          put(blynkWsLogin({
            username,
            hash: password
          }));
        } else {
          browserHistory.push('/login');
        }

        resolve(socket);

      });
      socket.addEventListener('error', (err) => {
        if (options.isDebugMode) {
          options.debug('onSocketError');
        }

        reject(err);
      });
    } catch (err) {
      if (options.isDebugMode) {
        options.debug('onSocketError');
      }

      reject(err);
    }
  });
}

function socketSubscribe(emitter) {
  socket.addEventListener('message', socket_message => {
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
      socketConnect().then((sock) => {
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

function* handleInput(socketPromise) {
  const socketChan = yield actionChannel(ACTIONS.WEBSOCKET_SEND);
  yield socketPromise;

  while (true) {
    const action = yield take(socketChan);

    if (!socket)
      throw new Error(`Cannot write WS. Socket doesn't exists`);

    if (options.isDebugMode) {
      options.debug('webSocketSend', action.value);
    }

    socket.send(action.value);
  }
}

function* handleOutput() {
  const socketEventChannel = yield call(subscribeToSocketEventChannel, socket);
  while (true) {
    const message = yield take(socketEventChannel);
    yield put(message);
  }
}

function* handleSocket(socketPromise) {
  yield fork(handleOutput);
  yield fork(handleInput, socketPromise);
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

    let socketPromise = yield call(connect);
    yield fork(handleSocket, socketPromise);
  }
}
