import {
  websocketConnect,
  websocketSend,
} from '../redux-websocket-middleware/actions';

import {
  blynkWsRequest
} from './actions';

import {Handlers} from './handlers';

let MSG_ID = 0;

export const RESPONSE_CODES = {
  OK             : 200,
  ILLEGAL_COMMAND: 2,
};

export const COMMANDS = {
  RESPONSE: 0,
  LOGIN   : 2,
  HARDWARE: 20,
  APP_SYNC: 25,
};

const blynkHeader = (msg_type, msg_id) => {
  return String.fromCharCode(
    msg_type,
    msg_id >> 8, msg_id & 0xFF
  );
};

const str2ab = (str) => {
  let buf = new ArrayBuffer(str.length); // 2 bytes for each char
  let bufView = new Uint8Array(buf);
  for (let i = 0, strLen = str.length; i < strLen; i++) {
    bufView[i] = str.charCodeAt(i);
  }
  return buf;
};

export const blynkWsConnect = (params) => {
  const {store, options} = params;

  if (options.isDebugMode)
    options.debug("BlynkWsConnect");

  return store.dispatch(websocketConnect());
};

export const blynkWsLogin = (params) => {

  const {store, action, options} = params;

  if (options.isDebugMode)
    options.debug("blynkWsLogin", action);

  const {user, hash} = action.value;

  const value = str2ab(
    blynkHeader(
      COMMANDS.LOGIN, ++MSG_ID
    ) + `${user}\0${hash}`
  );

  store.dispatch(blynkWsRequest({
    id     : MSG_ID,
    request: {
      command: COMMANDS.LOGIN,
      value  : `${user}\0${hash}`
    }
  }));

  store.dispatch(websocketSend(value));

};

export const blynkWsHardware = (params) => {

  const {store, action, options} = params;

  if (options.isDebugMode)
    options.debug("blynkWsHardware", action);

  const {deviceId, pin, value} = action.value;

  const request = str2ab(
    blynkHeader(
      COMMANDS.HARDWARE, ++MSG_ID
    ) + `${deviceId}\0vw\0${pin}\0${value}`
  );

  store.dispatch(blynkWsRequest({
    id     : MSG_ID,
    request: {
      command: COMMANDS.HARDWARE,
      value  : `${deviceId}\0vw\0${pin}\0${value}`
    }
  }));

  store.dispatch(websocketSend(request));

};

export const blynkWsMessage = (params) => {

  const {action, options, store} = params;

  const dataView = new DataView(action.value.data);

  const command = dataView.getUint8(0);

  const msgId = dataView.getUint16(1);

  if (options.isDebugMode)
    options.debug("blynkWsMessage", action, {
      command: command,
      msgId  : msgId,
    });

  let handlers = Handlers({
    action  : action,
    options : options,
    store   : store,
    dataView: dataView,
    command : command,
    msgId   : msgId
  });

  if (command === COMMANDS.RESPONSE) {

    handlers.ResponseHandler({
      responseCode: dataView.getUint32(3)
    });

  } else if (command === COMMANDS.HARDWARE) {

    handlers.HardwareHandler({
      msgId: ++MSG_ID
    });

  } else if (command === COMMANDS.APP_SYNC) {

    handlers.AppSyncHandler({
      msgId: ++MSG_ID
    });

  } else {

    handlers.UnknownCommandHandler();

  }
};
