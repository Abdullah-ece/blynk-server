// import { LOCATION_CHANGE } from 'react-router-redux';

import { eventChannel } from 'redux-saga';
import { put, take, call, fork } from 'redux-saga/effects';
import WS_ACTIONS from "./redux-websocket-middleware/actions";
import { Handlers } from "./blynk-websocket-middleware/handlers";
import {
  API_COMMANDS,
  COMMANDS,
  RESPONSE_CODES
} from "./blynk-websocket-middleware/commands";

// import {
//   getCommandKeyName,
// } from "./selectors";

export const INIT_ACTION_TYPE = 'CONNECT';

let MSG_ID = 0;
let socket_reconnect_retry = 0;
const MAX_SOCKET_RETRY = 5000;

/* eslint-disable no-constant-condition */

/* eslint-disable require-yield */
let socket;
let options;

function connect() {
  return new Promise((resolve, reject) => {
    try {
      socket = new WebSocket(options.endpoint);
      socket.addEventListener('open', () => {
        resolve(socket);
      });
      socket.addEventListener('error', (err) => {
        reject(err);
      });
    } catch (err) {
      reject(err);
    }
  });
}

function socketSubscribe(emitter) {
  socket.addEventListener('message', socket_message => {
    console.log('BBBBBBBBBBBBBBBBBB', socket_message);

    // const {action, options, store} = params;

    const dataView = new DataView(socket_message.data);

    const command = dataView.getUint8(0);

    const msgId = dataView.getUint16(1);

    let responseCode = -1;

    if (command === COMMANDS.RESPONSE) {
      responseCode = dataView.getUint32(3);
    }

    if (options.isDebugMode) {
      // options.debug("blynkWsMessage", action, {
      //   command: getCommandKeyName(command),
      //   msgId  : msgId,
      //   body: dataView.body
      // });
    }

    let handlers = Handlers({
      // action  : action,
      options: options,
      // store   : store,
      dataView: dataView,
      command: command,
      msgId: msgId
    });

    let message = null;

    // messages.forEach((msg) => {
    //   if (Number(msg.msgId) === Number(msgId) && message === null) {
    //     message = msg;
    //   }
    // });

    const API_COMMANDS_CODES_ARRAY = Object.keys(API_COMMANDS).map((key) => API_COMMANDS[key]);

    if (command === COMMANDS.RESPONSE && responseCode === RESPONSE_CODES.OK) {

      if (message && typeof message.promiseResolve === 'function')
        message.promiseResolve();

      handlers.ResponseOKHandler({
        responseCode: responseCode,
        message: message // there should be var "message", not var "message.previousAction". Just wrong naming, please keep it as it is
      });

    } else if (command === COMMANDS.HARDWARE) {

      handlers.HardwareHandler({
        msgId: ++MSG_ID
      });

    } else if (command === COMMANDS.UPDATE_DEVICE_METAFIELD) {

      handlers.DeviceMetadataUpdateHandler({
        msgId: ++MSG_ID
      });

    } else if (command === COMMANDS.LOG_EVENT) {

      handlers.LogEventHandler({
        msgId: ++MSG_ID
      });

    } else if (command === COMMANDS.LOG_EVENT_RESOLVE) {

      handlers.LogEventResolveHandler({
        msgId: ++MSG_ID
      });

    } else if (command === COMMANDS.DEVICE_CONNECTED) {

      handlers.DeviceConnectHandler({
        msgId: ++MSG_ID
      });

    } else if (command === COMMANDS.DEVICE_DISCONNECTED) {

      handlers.DeviceDisconnectHandler({
        msgId: ++MSG_ID
      });

    } else if (command === COMMANDS.APP_SYNC) {

      handlers.AppSyncHandler({
        msgId: ++MSG_ID
      });

    } else if (command === COMMANDS.CHART_DATA_FETCH) {

      handlers.ChartDataHandler({
        msgId: ++MSG_ID,
        previousAction: message,
      });

    } else if (command === COMMANDS.WEB_JSON) {

      handlers.JsonHandler({
        msgId: ++MSG_ID,
        previousAction: message && message.previousAction,
        promiseReject: message && message.promiseReject,
      });

    } else if (command === COMMANDS.RESPONSE && responseCode === RESPONSE_CODES.NO_DATA) {

      handlers.NoDataHandler({
        msgId: ++MSG_ID,
        previousAction: message,
      });

    } else if (command === API_COMMANDS.CREATE_DEVICE) {
      handlers.DeviceCreateHandler({
        msgId: ++MSG_ID,
        previousAction: message && message.previousAction,
        promiseResolve: message && message.promiseResolve,
      });

    } else if (API_COMMANDS_CODES_ARRAY.indexOf(command) >= 0) {

      handlers.ApiCallHandler({
        msgId: ++MSG_ID,
        previousAction: message && message.previousAction,
        promiseResolve: message && message.promiseResolve,
      });

    } else {

      handlers.UnknownCommandHandler();

    }
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
  while (true) {
    const action = yield take('*');
    console.log('AAAAAAAAAAAAAAAAAAAAAAAAAAAA', action);

    if (action.type === WS_ACTIONS.WEBSOCKET_SEND) {
      if (!socket)
        throw new Error(`Cannot write WS. Socket doesn't exists`);

      if (options.isDebugMode) {
        options.debug('webSocketSend', action.value);
      }

      socket.send(action.value);
    }
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
