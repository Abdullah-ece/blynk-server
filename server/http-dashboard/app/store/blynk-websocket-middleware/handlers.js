import {
  blynkWsResponse,
  blynkVW,
} from './actions';


const decodeBody = (dataView) => {
  const dec = new TextDecoder("utf-8");
  const body = dec.decode(new DataView(dataView.buffer, 3));

  return body;
};

export const Handlers = (params) => {

  const {store, options, action, command, msgId, dataView} = params;

  const responseHandler = ({ responseCode }) => {

    store.dispatch(blynkWsResponse({
      id      : msgId,
      response: {
        command     : command,
        responseCode: responseCode
      }
    }));

    if (options.isDebugMode)
      options.debug("blynkWsMessage Response", action, {
        command     : command,
        msgId       : msgId,
        responseCode: responseCode
      });
  };

  const hardwareHandler = ({ msgId }) => {

    // receive newly generated msgId because hardwareHandler has now own unique ID

    const body = decodeBody(dataView);

    const bodyArray = body.split('\0');

    if (options.isDebugMode)
      options.debug("blynkWsMessage Hardware", action, {
        command     : command,
        msgId       : msgId,
        bodyArray: bodyArray
      });

    store.dispatch(blynkWsResponse({
      id      : msgId,
      response: {
        command: command,
        body   : bodyArray
      }
    }));

    const deviceId = bodyArray[0].replace('0-', '');
    const pin = bodyArray[2];
    const value = bodyArray[3];

    store.dispatch(blynkVW({
      deviceId: Number(deviceId),
      pin: Number(pin),
      value: value
    }));
  };

  const unknownCommandHandler = () => {

    if (options.isDebugMode)
      options.debug("blynkWsMessage Unknown", action, {
        command     : command,
        msgId       : msgId,
      });

    store.dispatch(blynkWsResponse({
      id      : msgId,
      response: {
        command: command
      }
    }));
  };


  return {
    ResponseHandler: responseHandler,
    HardwareHandler: hardwareHandler,
    UnknownCommandHandler: unknownCommandHandler,
  };
};
