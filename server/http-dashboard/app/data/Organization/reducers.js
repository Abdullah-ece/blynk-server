const initialState = {
  name: ''
};

export default function Account(state = initialState, action) {
  switch (action.type) {

    case "API_ORGANIZATION_SUCCESS":
      return {
        ...state,
        ...action.payload.data
      };
    case "API_ORGANIZATION_FAILURE":
      return {
        ...state,
      };

    case "API_ORGANIZATION_SAVE_SUCCESS":
      return {
        ...state,
        ...action.payload.data
      };
    case "API_ORGANIZATION_SAVE_FAILURE":
      return {
        ...state,
      };

    case "ORGANIZATION_UPDATE_NAME":
      return {
        ...state,
        name: action.name
      };

    default:
      return state;
  }
}
