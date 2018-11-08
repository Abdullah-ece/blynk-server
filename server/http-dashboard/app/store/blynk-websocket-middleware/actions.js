export const ACTIONS = {
  'BLYNK_WS_TRACK_DEVICE_ID'        : 'BLYNK_WS_TRACK_DEVICE_ID',
  'BLYNK_WS_CHART_DATA_RESPONSE'    : 'BLYNK_WS_CHART_DATA_RESPONSE',
  'BLYNK_WS_CHART_DATA_FETCH'       : 'BLYNK_WS_CHART_DATA_FETCH',
  'BLYNK_WS_REQUEST'                : 'BLYNK_WS_REQUEST',
  'BLYNK_WS_RESPONSE'               : 'BLYNK_WS_RESPONSE',
  'BLYNK_WS_CONNECT'                : 'BLYNK_WS_CONNECT',
  'BLYNK_WS_DEVICE_METAFIELD_UPDATE': 'BLYNK_WS_DEVICE_METAFIELD_UPDATE',
  'BLYNK_WS_LOGIN'                  : 'BLYNK_WS_LOGIN',
  'BLYNK_WS_VIRTUAL_WRITE'          : 'BLYNK_WS_VIRTUAL_WRITE',
  'BLYNK_WS_HARDWARE'               : 'BLYNK_WS_HARDWARE',
  'BLYNK_WS_LOG_EVENT'              : 'BLYNK_WS_LOG_EVENT',
  'BLYNK_WS_LOG_EVENT_RESOLVE'      : 'BLYNK_WS_LOG_EVENT_RESOLVE',
  'BLYNK_WS_DEVICE_CONNECT'         : 'BLYNK_WS_DEVICE_CONNECT',
  'BLYNK_WS_DEVICE_DISCONNECT'      : 'BLYNK_WS_DEVICE_DISCONNECT',
  'BLYNK_WS_LOGIN_VIA_INVITE'       : 'BLYNK_WS_LOGIN_VIA_INVITE',
};

// export const blynkWsAddMessageToHistory = (message = {}) => {
export const blynkWsRequest = (message = {}) => {
  if (isNaN(Number(message.id)) || message.id === undefined)
  // throw new Error('Message id parameter is missing or wrong');

    if (!message.request) {
      throw new Error('Message parameter response is missed');
    }

  return {
    type : ACTIONS.BLYNK_WS_REQUEST,
    value: message
  };
};

export const blynkWsSetTrackDeviceId = (deviceId) => {
  return {
    type: ACTIONS.BLYNK_WS_TRACK_DEVICE_ID,
    value: {
      deviceId
    }
  };
};

// export const blynkWsUpdateMessageInHistory = (message = {}) => {
export const blynkWsResponse = (message = {}) => {

  if (isNaN(Number(message.id)) || message.id === undefined)
    throw new Error('Message id parameter is missing or wrong');

  if (!message.response) {
    throw new Error('Message parameter response is missed');
  }

  return {
    type : ACTIONS.BLYNK_WS_RESPONSE,
    value: message
  };
};

export const blynkVW = ({ deviceId, pin, value }) => ({
  type: ACTIONS.BLYNK_WS_VIRTUAL_WRITE,
  value: {
    deviceId,
    pin,
    value
  }
});

export const blynkWsConnect = () => ({
  type: ACTIONS.BLYNK_WS_CONNECT
});

export const blynkWsLogin = (params) => ({
  type : ACTIONS.BLYNK_WS_LOGIN,
  value: {
    user: params.username,
    hash: params.hash
  }
});

export const blynkWsLoginViaInvite = (params) => ({
  type : ACTIONS.BLYNK_WS_LOGIN_VIA_INVITE,
  value: {
    user: params.username,
    hash: params.hash
  }
});

export const blynkWsHardware = ({ deviceId, pin, value }) => ({
  type : ACTIONS.BLYNK_WS_HARDWARE,
  value: {
    deviceId,
    pin,
    value
  }
});

export const blynkWsLogEvent = ({ deviceId, eventCode, eventDescription }) => ({
  type : ACTIONS.BLYNK_WS_LOG_EVENT,
  value: {
    deviceId,
    eventCode,
    eventDescription,
  }
});

export const blynkWsLogEventResolve = ({ deviceId, logEventId, resolvedBy, resolveComment }) => ({
  type : ACTIONS.BLYNK_WS_LOG_EVENT_RESOLVE,
  value: {
    deviceId,
    logEventId,
    resolvedBy,
    resolveComment
  }
});

export const blynkWsDeviceConnect = ({ deviceId }) => ({
  type : ACTIONS.BLYNK_WS_DEVICE_CONNECT,
  value: {
    deviceId
  }
});

export const blynkWsDeviceDisconnect = ({ deviceId }) => ({
  type : ACTIONS.BLYNK_WS_DEVICE_DISCONNECT,
  value: {
    deviceId
  }
});

export const blynkWsChartDataFetch = ({ deviceId, widgetId, period, customRange }) => ({
  type : ACTIONS.BLYNK_WS_CHART_DATA_FETCH,
  value: {
    deviceId,
    widgetId,
    graphPeriod: period,
    customRange
  }
});

export const blynkWsDeviceMetadataUpdate = ({ deviceId, metafield }) => ({
  type: ACTIONS.BLYNK_WS_DEVICE_METAFIELD_UPDATE,
  value: {
    deviceId,
    metafield,
  }
});

export const blynkChartDataResponse = ({ deviceId, widgetId, points, graphPeriod }) => ({
  type: ACTIONS.BLYNK_WS_CHART_DATA_RESPONSE,
  value: {
    deviceId,
    widgetId,
    points,
    graphPeriod
  }
});


export default ACTIONS;
