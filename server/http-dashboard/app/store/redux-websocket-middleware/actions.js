export const ACTIONS = {
  /* actions fired by user */
  WEBSOCKET_CONNECT: "WEBSOCKET_CONNECT",
  WEBSOCKET_SEND: "WEBSOCKET_SEND",
  WEBSOCKET_DISCONNECT: "WEBSOCKET_DISCONNECT",

  /* actions fired by middleware */
  WEBSOCKET_CONNECTING: "WEBSOCKET_CONNECTING",
  WEBSOCKET_OPEN: 'WEBSOCKET_OPEN',
  WEBSOCKET_CLOSE: 'WEBSOCKET_CLOSE',
  WEBSOCKET_MESSAGE: 'WEBSOCKET_MESSAGE',
  WEBSOCKET_ERROR: 'WEBSOCKET_ERROR',
};

export const websocketConnect = () => {
  return {
    type: ACTIONS.WEBSOCKET_CONNECT
  };
};

export const websocketSend = (value) => {
  return {
    type: ACTIONS.WEBSOCKET_SEND,
    value: value
  };
};

export const websocketDisconnect = () => {
  return {
    type: ACTIONS.WEBSOCKET_DISCONNECT
  };
};

export const _websocketConnecting = () => {
  return {
    type: ACTIONS.WEBSOCKET_CONNECTING
  };
};

export const _websocketOpen = () => {
  return {
    type: ACTIONS.WEBSOCKET_OPEN
  };
};

export const _websocketClose = (value) => {
  return {
    type: ACTIONS.WEBSOCKET_CLOSE,
    value: value
  };
};

export const _websocketMessage = (value) => {
  return {
    type: ACTIONS.WEBSOCKET_MESSAGE,
    value: value
  };
};

export const _websocketError = (value) => {
  return {
    type: ACTIONS.WEBSOCKET_ERROR,
    value: value
  };
};

export default ACTIONS;
