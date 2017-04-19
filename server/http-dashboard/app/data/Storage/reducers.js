const initialState = {
  products: {
    isMetadataInfoRead: false
  }
};

export default function Account(state = initialState, action) {
  switch (action.type) {
    case "STORAGE_PRODUCTS_UPDATE_METADATA_INFO_READ":
      return {
        ...state,
        products: {
          ...state.products,
          isMetadataInfoRead: action.data
        }
      };

    default:
      return state;
  }
}

