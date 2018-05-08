export function ProductMetadataFieldAdd(data = {}) {
  return {
    type: 'PRODUCT_METADATA_FIELD_ADD',
    data: data
  };
}

export function ProductEditInfoValuesUpdate(data = {}) {
  return {
    type: 'PRODUCT_EDIT_INFO_VALUES_UPDATE',
    data: data
  };
}

export function ProductEditMetadataFieldsUpdate(data = {}) {
  return {
    type: 'PRODUCT_EDIT_METADATA_FIELDS_UPDATE',
    data: data
  };
}

export function ProductEditEventsFieldsUpdate(data = {}) {
  return {
    type: 'PRODUCT_EDIT_EVENTS_FIELDS_UPDATE',
    data: data
  };
}

export function ProductEditDataStreamsFieldUpdate(data = {}) {
  return {
    type: 'PRODUCT_EDIT_DATA_STREAMS_FIELD_UPDATE',
    data: data
  };
}

export function ProductEditDataStreamsFieldsUpdate(data = {}) {
  return {
    type: 'PRODUCT_EDIT_DATA_STREAMS_FIELDS_UPDATE',
    data: data
  };
}

export function ProductEditClearFields(data = {}) {
  return {
    type: 'PRODUCT_EDIT_CLEAR_FIELDS',
    data: data
  };
}

export function ProductEditMetadataFieldUpdate(data = {}) {
  return {
    type: 'PRODUCT_EDIT_METADATA_FIELD_UPDATE',
    data: data
  };
}

export function ProductMetadataFieldDelete(data = {}) {
  return {
    type: 'PRODUCT_METADATA_FIELD_DELETE',
    data: data
  };
}

export function ProductSetEdit(data = {}) {
  return {
    type: 'PRODUCT_SET_EDIT',
    data: data
  };
}

export function ProductMetadataFieldValuesUpdate(data = {}) {
  return {
    type: 'PRODUCT_METADATA_FIELD_VALUES_UPDATE',
    data: data
  };
}

export function ProductMetadataFieldInvalidFlagUpdate(data = {}) {
  return {
    type: 'PRODUCT_METADATA_FIELD_INVALID_FLAG_UPDATE',
    data: data
  };
}

export function ProductMetadataFieldsOrderUpdate(data = {}) {
  return {
    type: 'PRODUCT_METADATA_FIELDS_ORDER_UPDATE',
    data: data
  };
}

export function ProductMetadataUpdateInvalidFlag(data = {}) {
  return {
    type: 'PRODUCT_METADATA_UPDATE_INVALID_FLAG',
    data: data
  };
}

export function ProductInfoUpdateValues(data = {}) {
  return {
    type: 'PRODUCT_INFO_UPDATE_VALUES',
    data: data
  };
}

export function ProductInfoUpdateInvalidFlag(data = false) {
  return {
    type: 'PRODUCT_INFO_UPDATE_INVALID_FLAG',
    data: data
  };
}

export function ProductDashboardDeviceIdForPreviewChange(data = false) {
  return {
    type: 'PRODUCT_DASHBOARD_DEVICE_ID_FOR_PREVIEW_CHANGE',
    data: data
  };
}

export function ProductInfoOTADevicesSelectedDevicesUpdate(data = false) {
  return {
    type: 'PRODUCT_INFO_OTA_DEVICES_SELECTED_DEVICES_UPDATE',
    data: data
  };
}

export function ProductInfoOTAFirmwareUploadUpdate(data = {}) {
  return {
    type: 'PRODUCT_INFO_OTA_FIRMWARE_UPLOAD_UPDATE',
    data: data
  };
}

