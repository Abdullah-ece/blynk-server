import {
  websocketConnect,
  websocketSend,
} from '../redux-websocket-middleware/actions';

import {
  blynkWsRequest
} from './actions';

import {Handlers} from './handlers';

let MSG_ID = 0;

let messages = [
  // {
  //   id: MSG_ID,
  //   promise: Promise,
  //   resolve: Resolve,
  // }
];

export const RESPONSE_CODES = {
  OK             : 200,
  NO_DATA        : 17,
  ILLEGAL_COMMAND: 2,
};

export const COMMANDS = {
  RESPONSE: 0,
  LOGIN   : 2,
  DEVICE_CONNECTED: 4,
  HARDWARE: 20,
  APP_SYNC: 25,
  CHART_DATA_FETCH: 60,
  LOG_EVENT: 64,
  DEVICE_DISCONNECTED: 71,
  TRACK_DEVICE: 73,
  LOG_EVENT_RESOLVE: 75,
};

export const API_COMMANDS = {
  GET_ACCOUNT                   : 100,
  UPDATE_ACCOUNT                : 101,
  CREATE_DEVICE                 : 102,
  UPDATE_DEVICE                 : 103,
  GET_DEVICES                   : 104,
  GET_DEVICE                    : 105,
  GET_ORG                       : 106,
  GET_ORGS                      : 107,
  GET_ORG_USERS                 : 108,
  GET_ORG_LOCATIONS             : 109,
  CAN_INVITE_USER               : 110,
  UPDATE_ORG                    : 111,
  CREATE_PRODUCT                : 112,
  UPDATE_PRODUCT                : 113,
  DELETE_PRODUCT                : 114,
  GET_PRODUCT                   : 115,
  GET_PRODUCTS                  : 116,
  UPDATE_DEVICES_META_IN_PRODUCT: 117,
  UPDATE_USER_INFO              : 118,
  DELETE_USER                   : 119,
  CREATE_ORG                    : 120,
  DELETE_ORG                    : 122,
  CAN_DELETE_PRODUCT            : 123,
  INVITE_USER                   : 124,
  LOGIN_VIA_INVITE              : 125,
  UPDATE_DEVICE_METAFIELD       : 126,
  GET_DEVICE_TIMELINE           : 127,
  LOG_EVENT_RESOLVE             : 75,
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

export const blynkWsApiCall = (params) => {
  const {store, action, options} = params;

  if (options.isDebugMode)
    options.debug("blynkWsApiCall", action);

  const value = str2ab(
    blynkHeader(
      action.ws.request.command, ++MSG_ID
    ) + (action.ws.request.query || []).join('\0')
  );

  store.dispatch(blynkWsRequest({
    id     : MSG_ID,
    request: {
      command: action.ws.request.command,
      value  : (action.ws.request.query || []).join('\0')
    }
  }));

  let promiseResolve;

  let promise = new Promise((resolve) => {
    promiseResolve = resolve;

  messages.push({
    msgId         : MSG_ID,
    value         : {
      query: action.ws.request.query,
      body : action.ws.request.body,
    },
    promise       : promise,
    promiseResolve: promiseResolve,
    previousAction: action,
  });

});

  store.dispatch(websocketSend(value));

  return promise;

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

  let promiseResolve;
  let promise = new Promise((resolve) => {
    promiseResolve = resolve;
});

  messages.push({
    msgId: MSG_ID,
    promise: promise,
    promiseResolve: promiseResolve
  });

  store.dispatch(websocketSend(value));

  return promise;
};

export const blynkWsChartDataFetch = (params) => {

  const {store, action, options} = params;

  if (options.isDebugMode)
    options.debug("blynkWsChartDataFetch", action);

  const {deviceId, widgetId, graphPeriod, customRange} = action.value;

  const request = str2ab(
    blynkHeader(
      COMMANDS.CHART_DATA_FETCH, ++MSG_ID
    ) + `${deviceId}\0${widgetId}\0${graphPeriod}\0${customRange[0]}\0${customRange[1]}`
  );

  store.dispatch(blynkWsRequest({
    id     : MSG_ID,
    request: {
      command: COMMANDS.CHART_DATA_FETCH,
      value  : `${deviceId}\0${widgetId}\0${graphPeriod}\0${customRange[0]}\0${customRange[1]}`
    }
  }));

  messages.push({
    msgId: MSG_ID,
    value: {
      deviceId,
      widgetId,
      graphPeriod,
      customRange,
    }
  });

  store.dispatch(websocketSend(request));

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

export const blynkWsTrackDevice = (params) => {

  const {store, action, options} = params;

  if (options.isDebugMode)
    options.debug("blynkWsTrackDevice", action);

  const {deviceId} = action.value;

  const request = str2ab(
    blynkHeader(
      COMMANDS.TRACK_DEVICE, ++MSG_ID
    ) + `${deviceId}`
  );

  store.dispatch(blynkWsRequest({
    id     : MSG_ID,
    request: {
      command: COMMANDS.TRACK_DEVICE,
      value  : `${deviceId}`
    }
  }));

  store.dispatch(websocketSend(request));

};

export const blynkWsMessage = (params) => {

  const {action, options, store} = params;

  const dataView = new DataView(action.value.data);

  const command = dataView.getUint8(0);

  const msgId = dataView.getUint16(1);

  let responseCode = -1;

  if(command === COMMANDS.RESPONSE) {
    responseCode = dataView.getUint32(3);
  }

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

  let message = null;

  messages.forEach((msg) => {
    if(Number(msg.msgId) === Number(msgId) && message === null) {
    message = msg;
  }
});

  const API_COMMANDS_CODES_ARRAY = Object.keys(API_COMMANDS).map((key) => API_COMMANDS[key]);

  if (command === COMMANDS.RESPONSE && responseCode === RESPONSE_CODES.OK) {

    if(message && typeof message.promiseResolve === 'function')
      message.promiseResolve();

    handlers.ResponseOKHandler({
      responseCode  : responseCode,
      previousAction: message // there should be var "message", not var "message.previousAction". Just wrong naming, please keep it as it is
    });

  } else if (API_COMMANDS_CODES_ARRAY.indexOf(command) >= 0) {

    handlers.ApiCallHandler({
      msgId: ++MSG_ID,
      previousAction: message.previousAction,
      promiseResolve: message.promiseResolve,
    });

  } else if (command === COMMANDS.HARDWARE) {

    handlers.HardwareHandler({
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
      message,
    });

  } else if (command === COMMANDS.RESPONSE && responseCode === RESPONSE_CODES.NO_DATA) {

    handlers.NoDataHandler({
      msgId: ++MSG_ID,
      message,
    });

  } else {

    handlers.UnknownCommandHandler();

  }
};
