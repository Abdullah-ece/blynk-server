import WS_ACTIONS, {
  _websocketClose,
  _websocketError,
  _websocketOpen,
  _websocketMessage
} from './actions';

import { browserHistory } from 'react-router';

import { blynkWsLogin } from '../blynk-websocket-middleware/actions';

let pingInterval = null;

const onSocketOpen = (params) => {
  const { socket, resolve, store, options, loginRequired } = params;

  return () => {

    function keepAlive() {
      let timeout = options.pingTimeout || 1000;
      if (socket && socket.send) {
        socket.send('');
      }
      pingInterval = setTimeout(keepAlive, timeout);
    }

    if (options.isDebugMode) {
      options.debug('onSocketOpen');
    }
    
    store.dispatch(_websocketOpen());
    if (loginRequired) {
      const state = store.getState();
      if (state && state.Account && state.Account.credentials && state.Account.credentials.username) {
        const { username, password } = state.Account.credentials;
        store.dispatch(blynkWsLogin({
          username,
          hash: password
        }));
      } else {
        browserHistory.push('/login');
      }
    }

    resolve();

    if (options.ping !== false) {
      keepAlive();
    }

  };
};
const onSocketClose = (params) => {
  const { store, options } = params;

  return () => {

    if (options.isDebugMode) {
      options.debug('onSocketClose');
    }

    store.dispatch(_websocketClose());

  };
};
const onSocketMessage = (params) => {
  const { store, options } = params;

  return (evt) => {

    if (options.isDebugMode) {
      options.debug('onSocketMessage');
    }

    store.dispatch(_websocketMessage(evt));

  };
};
const onSocketError = (params) => {
  const { store, options } = params;

  return () => {

    if (options.isDebugMode) {
      options.debug('onSocketError');
    }

    store.dispatch(_websocketError());

    params.reject();

  };
};

const wsConnect = (params) => {

  const { options } = params;

  let socket = new WebSocket(options.defaultEndpoint);

  socket.binaryType = 'arraybuffer';
  socket.onopen = onSocketOpen({
    ...params,
    socket: socket,
    loginRequired: params.socket != null
  });
  socket.onclose = onSocketClose(params);
  socket.onmessage = onSocketMessage(params);
  socket.onerror = onSocketError(params);

  return socket;
};

const wsSend = (params) => {
  const { socket, action, options } = params;

  if (!socket)
    throw new Error(`Cannot write WS. Socket doesn't exists`);

  if (options.isDebugMode) {
    options.debug('webSocketSend', action.value);
  }

  socket.send(action.value);
};

export const createWsMiddleware = (options = {}) => {

  if (options.isDebugMode && !options.debug) {
    options.debug = function () {
      /* eslint-disable */
      console.log.apply(null, ['WebSocket Debug:', ...arguments]);
      /* eslint-enable */
    };
  }

  let socket = null;

  return store => next => action => {

    const params = {
      options: options,
      store: store,
      next: next,
      action: action,
      socket: socket,
    };

    if (action && action.type === WS_ACTIONS.WEBSOCKET_CONNECT || action.type === WS_ACTIONS.WEBSOCKET_CLOSE) {

      if (pingInterval)
        clearTimeout(pingInterval);

      next(action);
      return new Promise((resolve, reject) => {
        socket = wsConnect({
          ...params,
          resolve: resolve,
          reject: reject,
        });
      });
    }

    if (action && action.type === WS_ACTIONS.WEBSOCKET_SEND) {
      wsSend({
        ...params
      });
    }

    return next(action);
  };
};
