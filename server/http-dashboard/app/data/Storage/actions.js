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

export function LoginPageTermsAgreement(value=false){
  return {
    type: 'LoginPageTermsAgreement',
    value
  };
}
