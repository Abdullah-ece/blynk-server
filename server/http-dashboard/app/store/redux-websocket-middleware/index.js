import WS_ACTIONS, {
  _websocketClose,
  _websocketError,
  _websocketOpen,
  _websocketMessage
} from './actions';

const onSocketOpen = (params) => {
  const {socket, resolve, store, options} = params;

  return () => {

    function keepAlive() {
      if(options.isDebugMode) {
        options.debug('ping');
      }
      let timeout = options.pingTimeout || 10000;
      socket.send('');
      setTimeout(keepAlive, timeout);
    }

    if(options.isDebugMode) {
      options.debug('onSocketOpen');
    }

    store.dispatch(_websocketOpen());

    resolve();

    if(options.ping !== false) {
      keepAlive();
    }

  };
};
const onSocketClose = (params) => {
  const {store, options} = params;

  return () => {

    if(options.isDebugMode) {
      options.debug('onSocketClose');
    }

    store.dispatch(_websocketClose());

  };
};
const onSocketMessage = (params) => {
  const {store, options} = params;

  return (evt) => {

    if(options.isDebugMode) {
      options.debug('onSocketMessage');
    }

    store.dispatch(_websocketMessage(evt));

  };
};
const onSocketError = (params) => {
  const {store, options} = params;

  return () => {

    if(options.isDebugMode) {
      options.debug('onSocketError');
    }

    store.dispatch(_websocketError());

    params.resolve();

  };
};

const wsConnect = (params) => {

  const { options } = params;

  let socket = new WebSocket(options.defaultEndpoint);

  socket.binaryType = 'arraybuffer';
  socket.onopen = onSocketOpen(params);
  socket.onclose = onSocketClose(params);
  socket.onmessage = onSocketMessage(params);
  socket.onerror = onSocketError(params);

  return socket;
};

const wsSend = (params) => {
  const { socket, action, options } = params;

  if(!socket)
    throw new Error(`Cannot write WS. Socket doesn't exists`);

  if(options.isDebugMode) {
    options.debug('webSocketSend', action.value);
  }

  socket.send(action.value);
};

export const createWsMiddleware = (options = {}) => {

  if(options.isDebugMode && !options.debug) {
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

    if(action && action.type === WS_ACTIONS.WEBSOCKET_CONNECT) {
      next(action);
      return new Promise((resolve) => {
        socket = wsConnect({
          ...params,
          resolve: resolve,
        });
      });
    }

    if(action && action.type === WS_ACTIONS.WEBSOCKET_SEND) {
      wsSend({
        ...params
      });
    }

    return next(action);
  };
};
