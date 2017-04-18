const initialState = {
  products: []
};

export default function Account(state = initialState, action) {
  switch (action.type) {

    case "API_PRODUCTS_SUCCESS":
      return {
        ...state,
        products: action.payload.data
      };

    default:
      return state;
  }
}
