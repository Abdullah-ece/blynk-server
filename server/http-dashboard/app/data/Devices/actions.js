// here should be actions

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
