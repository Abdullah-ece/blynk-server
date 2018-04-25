import wsActions from '../redux-websocket-middleware/actions';
import blynkWsActions from './actions';
import {
  blynkWsConnect,
  blynkWsLogin,
  blynkWsMessage,
  blynkWsHardware,
  blynkWsTrackDevice,
  blynkWsChartDataFetch
} from './commands';

export const createBlynkWsMiddleware = (options = {}) => {

  if (options.isDebugMode && !options.debug) {
    options.debug = function () {
      /* eslint-disable */
      console.log.apply(null, ['BlynkSocket Debug:', ...arguments]);
      /* eslint-enable */
    };
  }

  let connectionReadyResolve = null;

  let connectionReadyPromise = new Promise((resolve) => {
    connectionReadyResolve = resolve;
  });

  return store => next => action => {
    const execCommand = (command, ...params) => {

      if(connectionReadyPromise) {
        connectionReadyPromise.then(() => {
          command.apply(this, params);
        });
      } else {
        command.apply(this, params);
      }

    };

    const params = {
      store,
      next,
      action,
      options
    };

    /*
      use execCommand(cmd, params) if your command should be executed
      when connection ready and user logged in
    */

    if (action && action.type === blynkWsActions.BLYNK_WS_CONNECT) {
      return blynkWsConnect(params);
    }

    if (action && action.type === wsActions.WEBSOCKET_MESSAGE) {
      blynkWsMessage(params);
    }

    if (action && action.type === blynkWsActions.BLYNK_WS_LOGIN) {
      return blynkWsLogin(params).then(() => {
        connectionReadyResolve();
      });
    }

    if (action && action.type === blynkWsActions.BLYNK_WS_HARDWARE) {
      execCommand(blynkWsHardware, params);
    }

    if (action && action.type === blynkWsActions.BLYNK_WS_CHART_DATA_FETCH) {
      execCommand(blynkWsChartDataFetch, params);
    }

    if (action && action.type === blynkWsActions.BLYNK_WS_TRACK_DEVICE_ID) {
      execCommand(blynkWsTrackDevice, params);
    }

    return next(action);

  };

};
