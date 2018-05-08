const initialState = {
  products: {
    metadataFirstTime: false
  },
  deviceSmartSearch: false,
  loginPageTermsAgreement: false,
  OTAUpdate: {
    title: null,
    selectedDevicesIds: [],
    pathToFirmware: null,
    firmwareFields: {},
    firmwareFileName: null,
    productId: -1,
    status: false
  }
};

export default function Account(state = initialState, action) {
  switch (action.type) {
    case "STORAGE_PRODUCTS_UPDATE_METADATA_FIRST_TIME_FLAG":
      return {
        ...state,
        products: {
          ...state.products,
          metadataFirstTime: action.data
        }
      };

    case 'DeviceSmartSearch':
      return {
        ...state,
        deviceSmartSearch: action.value
      };

    case 'LoginPageTermsAgreement':
      return {
        ...state,
        loginPageTermsAgreement: action.value
      };

    case "STORAGE_OTA_DEVICES_SESSION_START":
      return {
        ...state,
        OTAUpdate: {
          ...state.OTAUpdate,
          ...action.value,
          status: true,
        }
      };

    case "STORAGE_OTA_DEVICES_SESSION_STOP":
      return {
        ...state,
        OTAUpdate: {
          ...state.OTAUpdate,
          title: null,
          selectedDevicesIds: [],
          pathToFirmware: null,
          firmwareFields: {},
          firmwareFileName: null,
          productId: -1,
          status: false
        }
      };


    default:
      return state;
  }
}
