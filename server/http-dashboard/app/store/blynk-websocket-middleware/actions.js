export const ACTIONS = {
  'BLYNK_WS_TRACK_DEVICE_ID': 'BLYNK_WS_TRACK_DEVICE_ID',
  'BLYNK_WS_REQUEST'        : 'BLYNK_WS_REQUEST',
  'BLYNK_WS_RESPONSE'       : 'BLYNK_WS_RESPONSE',
  'BLYNK_WS_CONNECT'        : 'BLYNK_WS_CONNECT',
  'BLYNK_WS_LOGIN'          : 'BLYNK_WS_LOGIN',
  'BLYNK_WS_VIRTUAL_WRITE'  : 'BLYNK_WS_VIRTUAL_WRITE',
  'BLYNK_WS_HARDWARE'       : 'BLYNK_WS_HARDWARE',
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

export const blynkWsHardware = ({ deviceId, pin, value }) => ({
  type : ACTIONS.BLYNK_WS_HARDWARE,
  value: {
    deviceId,
    pin,
    value
  }
});

export default ACTIONS;
