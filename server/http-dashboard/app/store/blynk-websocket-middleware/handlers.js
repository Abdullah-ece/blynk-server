import {
  blynkVW,
  blynkWsResponse,
  blynkWsLogEvent,
  blynkWsDeviceConnect,
  blynkWsDeviceDisconnect,
  blynkChartDataResponse,
} from './actions';

import {
  getTrackDeviceId,
  getTrackOnlyByDeviceId
} from './selectors';


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

    const trackDeviceId = getTrackDeviceId(store.getState());
    const trackOnlyByDeviceId = getTrackOnlyByDeviceId(store.getState());

    const body = decodeBody(dataView);

    const bodyArray = body.split('\0');

    const deviceId = bodyArray[0].replace('0-', '');
    const pin = bodyArray[2];
    const value = bodyArray[3];

    const deviceIdEqualTrackDeviceId = Number(trackDeviceId) === Number(deviceId);

    if (options.isDebugMode)
      options.debug("blynkWsMessage Hardware", action, {
        command     : command,
        msgId       : msgId,
        bodyArray: bodyArray,
        trackDeviceId,
        trackOnlyByDeviceId,
        deviceIdEqualTrackDeviceId
      });

    if(trackOnlyByDeviceId && !deviceIdEqualTrackDeviceId)
      return false;

    store.dispatch(blynkWsResponse({
      id      : msgId,
      response: {
        command: command,
        body   : bodyArray
      }
    }));

    store.dispatch(blynkVW({
      deviceId: Number(deviceId),
      pin: Number(pin),
      value: value
    }));
  };

  const logEventHandler = ({ msgId }) => {

    const body = decodeBody(dataView);

    const bodyArray = body.split('\0');

    const deviceId = bodyArray[0];
    const eventCode = bodyArray[1];

    if (options.isDebugMode)
      options.debug("blynkWsMessage LogEvent", action, {
        command     : command,
        msgId       : msgId,
        bodyArray: bodyArray,
        deviceId,
        eventCode
      });

    store.dispatch(blynkWsLogEvent({
      deviceId,
      eventCode,
    }));

  };

  const deviceConnectHandler = ({ msgId }) => {

    const body = decodeBody(dataView);

    const bodyArray = body.split('\0');

    const deviceId = bodyArray[0].replace('0-', '');

    if (options.isDebugMode)
      options.debug("blynkWsMessage DeviceConnect", action, {
        command     : command,
        msgId       : msgId,
        bodyArray: bodyArray,
        deviceId
      });

    store.dispatch(blynkWsDeviceConnect({
      deviceId
    }));

  };

  const deviceDisconnectHandler = ({ msgId }) => {

    const body = decodeBody(dataView);

    const bodyArray = body.split('\0');

    const deviceId = bodyArray[0].replace('0-', '');

    if (options.isDebugMode)
      options.debug("blynkWsMessage DeviceDisconnect", action, {
        command     : command,
        msgId       : msgId,
        bodyArray: bodyArray,
        deviceId
      });

    store.dispatch(blynkWsDeviceDisconnect({
      deviceId
    }));

  };


  const appSyncHandler = ({ msgId }) => {

    const body = decodeBody(dataView);

    const bodyArray = body.split('\0');

    if (options.isDebugMode)
      options.debug("blynkWsMessage AppSync", action, {
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

    const deviceId = bodyArray[0];
    const pin = bodyArray[2];
    const value = bodyArray[3];

    store.dispatch(blynkVW({
      deviceId: Number(deviceId),
      pin: Number(pin),
      value: value
    }));

  };

  const chartDataHandler = ({ msgId, previousAction }) => {

    const DEVICE_ID_OFFSET = 3;
    const POINTS_COUNT_OFFSET = DEVICE_ID_OFFSET + 4;
    const POINTS_DATA_OFFSET = POINTS_COUNT_OFFSET + 4;

    const POINT_DATA_VALUE_BYTES = 8;
    const POINT_DATA_TIMESTAMP_BYTES = 8;

    const POINT_DATA_VALUE_OFFSET = (i) => (POINT_DATA_VALUE_BYTES+POINT_DATA_TIMESTAMP_BYTES)*i;
    const POINT_DATA_TIMESTAMP_OFFSET = (i) => (POINT_DATA_VALUE_OFFSET(i) + POINT_DATA_VALUE_BYTES);

    const getTimestampLong = (data, i) => {
      const MAGIC_NUMBER_TO_READ_TIMESTAMP = 12;
      return data.getUint32(POINT_DATA_TIMESTAMP_OFFSET(i) & 0x001FFFFF) * 4294967296 + data.getUint32((POINT_DATA_VALUE_OFFSET(i))+MAGIC_NUMBER_TO_READ_TIMESTAMP);
    };

    let deviceId = dataView.getUint32(DEVICE_ID_OFFSET);
    let widgetId = previousAction.value.widgetId;
    let graphPeriod = previousAction.value.graphPeriod;
    let pointsCount = dataView.getUint32(POINTS_COUNT_OFFSET); // deviceId = 4 bytes // pointsCount = 60 dots

    let pointData = new DataView(dataView.buffer, POINTS_DATA_OFFSET);

    const points = [];

    for(let i = 0; i < pointsCount; i++) {
      let point = pointData.getFloat64(POINT_DATA_VALUE_OFFSET(i));
      let timestamp = getTimestampLong(pointData, i);

      points.push({
        x: Number(timestamp),
        y: Number(point),
        date: new Date(timestamp).toString()
      });
    }

    if (options.isDebugMode)
      options.debug("blynkWsMessage ChartData", action, {
        command     : command,
        msgId       : msgId,
        bodyArray: `${deviceId} ${pointsCount} ${JSON.stringify(points)}`
      });

    store.dispatch(blynkWsResponse({
      id      : msgId,
      response: {
        command: command,
        body   : `${deviceId} ${pointsCount} ${JSON.stringify(points)}`
      }
    }));

    store.dispatch(blynkChartDataResponse({
      deviceId,
      widgetId,
      graphPeriod,
      points,
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
    LogEventHandler: logEventHandler,
    DeviceConnectHandler: deviceConnectHandler,
    DeviceDisconnectHandler: deviceDisconnectHandler,
    AppSyncHandler: appSyncHandler,
    ChartDataHandler: chartDataHandler,
    UnknownCommandHandler: unknownCommandHandler,
  };
};
