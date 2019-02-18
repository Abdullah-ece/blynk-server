import { getCommandKeyName } from "./selectors";

import {
  websocketConnect,
  websocketSend,
  blynkWsRequest
} from './actions';

import { Handlers } from './handlers';

let MSG_ID = 0;

let messages = [
  // {
  //   id: MSG_ID,
  //   promise: Promise,
  //   resolve: Resolve,
  // }
];

export const RESPONSE_CODES = {
  OK: 200,
  NO_DATA: 17,
  ILLEGAL_COMMAND: 2,
};

export const COMMANDS = {
  RESPONSE: 0,
  LOGIN: 2,
  LOGIN_VIA_INVITE: 125,
  DEVICE_CONNECTED: 4,
  HARDWARE: 20,
  APP_SYNC: 25,
  CHART_DATA_FETCH: 60,
  LOG_EVENT: 64,
  DEVICE_DISCONNECTED: 71,
  TRACK_DEVICE: 73,
  LOG_EVENT_RESOLVE: 75,
  UPDATE_DEVICE_METAFIELD: 126,
  WEB_JSON: 99,
};

export const API_COMMANDS = {
  RESET_PASSWORD: 81,
  GET_ACCOUNT: 100,
  UPDATE_ACCOUNT: 101,
  CREATE_DEVICE: 102,
  UPDATE_DEVICE: 103,
  GET_DEVICES: 104,
  GET_DEVICE: 105,
  GET_ORG: 106,
  GET_ORG_HIERARCHY: 132,
  GET_ORGS: 107,
  GET_ORG_USERS: 108,
  GET_ORG_LOCATIONS: 109,
  CAN_INVITE_USER: 110,
  UPDATE_ORG: 111,
  CREATE_PRODUCT: 112,
  UPDATE_PRODUCT: 113,
  DELETE_PRODUCT: 114,
  GET_PRODUCT: 115,
  GET_PRODUCTS: 116,
  UPDATE_DEVICES_META_IN_PRODUCT: 117,
  UPDATE_USER_INFO: 118,
  DELETE_USER: 119,
  CREATE_ORG: 120,
  DELETE_ORG: 122,
  CAN_DELETE_PRODUCT: 123,
  INVITE_USER: 124,
  UPDATE_DEVICE_METAFIELD: 126,
  GET_DEVICE_TIMELINE: 127,
  LOG_EVENT_RESOLVE: 75,
  DELETE_DEVICE: 128,
  LOGOUT: 66,
  WEB_GET_TEMP_SECURE_TOKEN: 131,
  WEB_SET_AUTH_TOKEN: 138,
  WEB_EDIT_OWN_ORG: 139,
  WEB_TRACK_ORG: 145,
  WEB_CREATE_ROLE: 133, // accepts role json
  WEB_UPDATE_ROLE: 134, // accepts role json
  WEB_GET_ROLE: 135, // accepts roleId
  WEB_GET_ROLES: 136, // no params required
  WEB_DELETE_ROLE: 137, // accepts roleId
  WEB_GET_USER_COUNTERS_BY_ROLE: 146,
  WEB_GET_DEVICES_BY_REFERENCE_METAFIELD: 147,
  WEB_GET_RULE_GROUP: 150,
  WEB_EDIT_RULE_GROUP: 151,
  WEB_SHIPMENT_START: 141, // accepts json string otaDTO
  WEB_SHIPMENT_STOP: 142, // accepts json_string otaDTO
  WEB_SHIPMENT_GET_FIRMWARE_INFO: 143,//  "path_to_firmware"
  WEB_SHIPMENT_DELETE: 144, // "shipmentId"
  WEB_GET_ORG_SHIPMENTS: 153,
  WEB_SHIPMENT_STATUS: 160
};

const blynkHeader = (msg_type, msg_id) => {
  return new Uint8Array(
    [
      msg_type,
      msg_id >> 8,
      msg_id & 0xFF
    ]
  );
};

const str2ab = (payload) => {
  let encoder = new TextEncoder();
  return encoder.encode(payload); // returns Uint8Array
};

const makeMessage = (msg_type, msg_id, payload) => {
  let header = blynkHeader(msg_type, msg_id);
  //todo optimize, overhead, requires 2x memory
  let bodyBytes = str2ab(payload);
  let result = new Uint8Array(header.length + bodyBytes.length);
  result.set(header);
  result.set(bodyBytes, header.length);
  return result;
};

export const blynkWsConnect = (params) => {
  const { store, options } = params;

  if (options.isDebugMode)
    options.debug("BlynkWsConnect");

  return store.dispatch(websocketConnect());
};

export const blynkWsApiCall = (params) => {

  MSG_ID = ++MSG_ID;

  const { store, action, options } = params;

  if (options.isDebugMode)
    options.debug("blynkWsApiCall", action, MSG_ID);

  const value = makeMessage(
    action.ws.request.command, MSG_ID, (action.ws.request.query || []).join('\0')
  );

  store.dispatch(blynkWsRequest({
    id: MSG_ID,
    request: {
      command: action.ws.request.command,
      value: (action.ws.request.query || []).join('\0')
    }
  }));

  let promiseResolve;
  let promiseReject;

  let promise = new Promise((resolve, reject) => {
    promiseResolve = resolve;
    promiseReject = reject;

    messages.push({
      msgId: MSG_ID,
      value: {
        query: action.ws.request.query,
        body: action.ws.request.body,
      },
      promise: promise,
      promiseResolve: promiseResolve,
      promiseReject: promiseReject,
      previousAction: action,
    });

  });

  store.dispatch(websocketSend(value));

  return promise;

};

export const blynkWsLogin = (params, command = COMMANDS.LOGIN,) => {

  const { store, action, options } = params;

  if (options.isDebugMode)
    options.debug("blynkWsLogin", action);

  const { user, hash } = action.value;

  const value = makeMessage(
    command, ++MSG_ID, `${user}\0${hash}`
  );

  store.dispatch(blynkWsRequest({
    id: MSG_ID,
    request: {
      command: command,
      value: `${user}\0${hash}`
    }
  }));

  let promiseResolve;
  let promiseReject;

  let promise = new Promise((resolve, reject) => {
    promiseResolve = resolve;
    promiseReject = reject;
  });

  messages.push({
    msgId: MSG_ID,
    promise: promise,
    promiseResolve: promiseResolve,
    promiseReject: promiseReject,
  });

  store.dispatch(websocketSend(value));

  return promise;
};

export const blynkWsChartDataFetch = (params) => {

  const { store, action, options } = params;

  if (options.isDebugMode)
    options.debug("blynkWsChartDataFetch", action);

  const { deviceId, widgetId, graphPeriod, customRange } = action.value;

  const request = makeMessage(
    COMMANDS.CHART_DATA_FETCH, ++MSG_ID,
    `${deviceId}\0${widgetId}\0${graphPeriod}\0${customRange[0]}\0${customRange[1]}`
  );

  store.dispatch(blynkWsRequest({
    id: MSG_ID,
    request: {
      command: COMMANDS.CHART_DATA_FETCH,
      value: `${deviceId}\0${widgetId}\0${graphPeriod}\0${customRange[0]}\0${customRange[1]}`
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

  const { store, action, options } = params;

  if (options.isDebugMode)
    options.debug("blynkWsHardware", action);

  const { deviceId, pin, value } = action.value;

  const request = makeMessage(
    COMMANDS.HARDWARE, ++MSG_ID, `${deviceId}\0vw\0${pin}\0${value}`
  );

  store.dispatch(blynkWsRequest({
    id: MSG_ID,
    request: {
      command: COMMANDS.HARDWARE,
      value: `${deviceId}\0vw\0${pin}\0${value}`
    }
  }));

  store.dispatch(websocketSend(request));

};

export const blynkWsTrackDevice = (params) => {

  const { store, action, options } = params;

  if (options.isDebugMode)
    options.debug("blynkWsTrackDevice", action);

  const { deviceId } = action.value;

  const request = makeMessage(
    COMMANDS.TRACK_DEVICE, ++MSG_ID, `${deviceId}`
  );

  store.dispatch(blynkWsRequest({
    id: MSG_ID,
    request: {
      command: COMMANDS.TRACK_DEVICE,
      value: `${deviceId}`
    }
  }));

  store.dispatch(websocketSend(request));

};

export const blynkWsMessage = (params) => {

  const { action, options, store } = params;

  const dataView = new DataView(action.value.data);

  const command = dataView.getUint8(0);

  const msgId = dataView.getUint16(1);

  let responseCode = -1;

  if (command === COMMANDS.RESPONSE) {
    responseCode = dataView.getUint32(3);
  }

  if (options.isDebugMode)
    options.debug("blynkWsMessage", action, {
      command: getCommandKeyName(command),
      msgId: msgId,
      body: dataView.body
    });

  let handlers = Handlers({
    action: action,
    options: options,
    store: store,
    dataView: dataView,
    command: command,
    msgId: msgId
  });

  let message = null;

  messages.forEach((msg) => {
    if (Number(msg.msgId) === Number(msgId) && message === null) {
      message = msg;
    }
  });

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
};
