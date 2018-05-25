export function ProductsUpdateMetadataFirstTime(data) {
  return {
    type: 'STORAGE_PRODUCTS_UPDATE_METADATA_FIRST_TIME_FLAG',
    data: data
  };
}

export function DeviceSmartSearchState(value=false){
  return {
    type: 'DeviceSmartSearch',
    value
  };
}

export function StorageOTADevicesSessionStart(value={}){
  return {
    type: 'STORAGE_OTA_DEVICES_SESSION_START',
    value
  };
}

export function StorageOTADevicesSessionStop(){
  return {
    type: 'STORAGE_OTA_DEVICES_SESSION_STOP'
  };
}

export function StorageDevicesSortChange(value=false){
  return {
    type: 'STORAGE_DEVICES_SORT_CHANGE',
    value
  };
}

export function StorageRememberRequestedPage(value=false){
  return {
    type: 'STORAGE_REMEMBER_REQUESTED_PAGE',
    value
  };
}
