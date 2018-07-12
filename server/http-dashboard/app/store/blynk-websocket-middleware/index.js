import wsActions from '../redux-websocket-middleware/actions';
import blynkWsActions from './actions';
import {
  blynkWsConnect,
  blynkWsLogin,
  blynkWsMessage,
  blynkWsHardware,
  blynkWsTrackDevice,
  blynkWsChartDataFetch,
  blynkWsApiCall,
  API_COMMANDS,
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

        return new Promise((resolve) => {
          connectionReadyPromise.then(() => {
            let response = command.apply(this, params);

            if(response && response.then) {
              response.then((data) => {
                resolve(data);
              });
            }
          });
        });
      } else {
        return command.apply(this, params);
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

    // api commands

    if (action && action.ws && action.ws.request) {

      const API_COMMANDS_CODES_ARRAY = Object.keys(API_COMMANDS).map((key) => API_COMMANDS[key]);

      if(action.ws.request && !action.ws.request.command) {
        throw new Error('Command for WS API call is not specified');
      }

      if(action.ws.request.command && API_COMMANDS_CODES_ARRAY.indexOf(action.ws.request.command) === -1 ) {
        throw new Error(`Command "${action.ws.request.command}" for WS API call is not listed in API COMMANDS`);
      }

      next(action);

      return execCommand(blynkWsApiCall, params);

    }

    return next(action);

  };

};
