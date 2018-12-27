import {API_COMMANDS} from "store/blynk-websocket-middleware/commands";

export function DevicesSortChange(value) {
  return {
    type: 'DEVICES_SORT_CHANGE',
    value: value
  };
}

export function DevicesListFilterValueChange(value) {
  return {
    type: 'DEVICES_LIST_FILTER_VALUE_CHANGE',
    value,
  };
}

export function DeviceDetailsUpdate(value) {
  return {
    type: 'DEVICES_DEVICE_DETAILS_UPDATE',
    value: value
  };
}

export function DeviceTimeFilterUpdate(value) {
  return {
    type: 'DEVICES_TIME_FILTER_UPDATE',
    value: value
  };
}

export function DeviceListNameUpdate({deviceId, name}) {
  return {
    type: 'DEVICES_LIST_NAME_UPDATE',
    value: {
      deviceId,
      name
    }
  };
}

export function DeviceCreateUpdate(value) {
  return {
    type: 'DeviceCreateUpdate',
    value: value
  };
}

export function DeviceTimelineControlsUpdate(params) {
  return {
    type: 'DEVICE_TIMELINE_CONTROLS_UPDATE',
    value: params
  };
}

export function GetDeviceByReferenceMetafield(params) {
  // return {
  //   type: API_COMMANDS.WEB_GET_DEVICES_BY_REFERENCE_METAFIELD,
  //   value: params
  // };

  const {deviceId, metafieldId} = params;

  return {
    type: 'WEB_GET_DEVICES_BY_REFERENCE_METAFIELD',
    ws: {
      request: {
        command: API_COMMANDS.WEB_GET_DEVICES_BY_REFERENCE_METAFIELD,
        query: [
          deviceId, metafieldId
        ],
      }
    }
  };
}

