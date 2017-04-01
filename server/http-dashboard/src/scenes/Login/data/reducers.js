export default function Login(state = {}, action) {

  switch (action.type) {
    case "API_LOGIN_SUCCESS":
      return {
        ...state,
        token: action.payload.data.token
      };
    case "API_LOGIN_FAILURE":
      return {};
    default:
      return state;
  }

}
