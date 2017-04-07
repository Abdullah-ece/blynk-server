const initialState = {
  name: '',
  email: '',
  role: ''
};

export default function Account(state = initialState, action) {
  switch (action.type) {

    case "API_ACCOUNT_SUCCESS":
      return {
        ...state,
        ...action.payload.data
      };
    case "API_ACCOUNT_FAILURE":
      return {
        ...state,
      };

    case "API_ACCOUNT_SAVE_SUCCESS":
      return {
        ...state,
        ...action.payload.data
      };
    case "API_ACCOUNT_SAVE_FAILURE":
      return {
        ...state,
      };

    case "ACCOUNT_UPDATE_NAME":
      return {
        ...state,
        name: action.name
      };

    default:
      return state;
  }
}
