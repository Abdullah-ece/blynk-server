const initialState = {
  products: {
    metadataFirstTime: false
  },
  deviceSmartSearch: false,
  devicesListSorting: {
    value: "REQUIRE_ATTENTION",
  },
  requestedPage: '/login',
  OTAUpdate: {
    title: null,
    selectedDevicesIds: [],
    pathToFirmware: null,
    firmwareFields: {},
    firmwareFileName: null,
    productId: -1,
    status: 0 // 0 - not started, 1 - in progress, 2 - finished
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

    case "STORAGE_OTA_DEVICES_SESSION_START":
      return {
        ...state,
        OTAUpdate: {
          ...state.OTAUpdate,
          ...action.value,
          status: 1,
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
          status: 2
        }
      };

    case "STORAGE_DEVICES_SORT_CHANGE":
      return {
        ...state,
        devicesListSorting:{
          value: action.value
        }
      };

    case "STORAGE_REMEMBER_REQUESTED_PAGE":
      return {
        ...state,
        requestedPage: action.value
      };


    default:
      return state;
  }
}
