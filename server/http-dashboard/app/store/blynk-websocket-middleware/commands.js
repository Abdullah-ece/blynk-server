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

  const {deviceId, widgetId, graphPeriod} = action.value;

  const request = str2ab(
    blynkHeader(
      COMMANDS.CHART_DATA_FETCH, ++MSG_ID
    ) + `${deviceId}\0${widgetId}\0${graphPeriod}`
  );

  store.dispatch(blynkWsRequest({
    id     : MSG_ID,
    request: {
      command: COMMANDS.CHART_DATA_FETCH,
      value  : `${deviceId}\0${widgetId}\0${graphPeriod}`
    }
  }));

  messages.push({
    msgId: MSG_ID,
    value: {
      deviceId,
      widgetId,
      graphPeriod
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

  let previousAction = null;

  messages.forEach((message) => {
    if(Number(message.msgId) === Number(msgId) && typeof message.promiseResolve === 'function') {
      message.promiseResolve();
    }

    if(Number(message.msgId) === Number(msgId) && previousAction === null) {
      previousAction = message;
    }
  });

  if (command === COMMANDS.RESPONSE && responseCode === RESPONSE_CODES.OK) {

    handlers.ResponseOKHandler({
      responseCode: responseCode,
      previousAction: previousAction
    });

  } else if (command === COMMANDS.HARDWARE) {

    handlers.HardwareHandler({
      msgId: ++MSG_ID
    });

  } else if (command === COMMANDS.LOG_EVENT) {

    handlers.LogEventHandler({
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
      previousAction,
    });

  } else if (command === COMMANDS.RESPONSE && responseCode === RESPONSE_CODES.NO_DATA) {

    handlers.NoDataHandler({
      msgId: ++MSG_ID,
      previousAction,
    });

  } else {

    handlers.UnknownCommandHandler();

  }
};
