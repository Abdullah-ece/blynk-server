import wsActions from '../redux-websocket-middleware/actions';
import blynkWsActions from './actions';
import {
  blynkWsConnect,
  blynkWsLogin,
  blynkWsMessage,
  blynkWsHardware,
} from './commands';

export const createBlynkWsMiddleware = (options = {}) => {

  if (options.isDebugMode && !options.debug) {
    options.debug = function () {
      /* eslint-disable */
      console.log.apply(null, ['BlynkSocket Debug:', ...arguments]);
      /* eslint-enable */
    };
  }

  return store => next => action => {

    const params = {
      store,
      next,
      action,
      options
    };

    if (action && action.type === blynkWsActions.BLYNK_WS_CONNECT) {
      return blynkWsConnect(params);
    }

    if (action && action.type === wsActions.WEBSOCKET_MESSAGE) {
      blynkWsMessage(params);
    }

    if (action && action.type === blynkWsActions.BLYNK_WS_LOGIN) {
      blynkWsLogin(params);
    }

    if (action && action.type === blynkWsActions.BLYNK_WS_HARDWARE) {
      blynkWsHardware(params);
    }

    return next(action);

  };

};
