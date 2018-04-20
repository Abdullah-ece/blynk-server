const initialState = {
  products: {
    metadataFirstTime: false
  },
  deviceSmartSearch: false,
  loginPageTermsAgreement: false,
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

    default:
      return state;
  }
}
