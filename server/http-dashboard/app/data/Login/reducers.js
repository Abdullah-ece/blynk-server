export default function Login(state = { isLoggedIn: false }, action) {
  switch (action.type) {

    case "API_LOGIN_SUCCESS":
      return {
        ...state,
        isLoggedIn: true
      };
    case "API_LOGIN_FAILURE":
      return {
        ...state,
        isLoggedIn: false
      };

    case "API_LOGOUT_SUCCESS":
      return {
        ...state,
        isLoggedIn: false
      };

    case "API_LOGOUT_FAILURE":
      return {
        ...state,
        isLoggedIn: false
      };

    case "API_RESET_SUCCESS":
      return {
        ...state,
        isLoggedIn: false
      };

    case "API_RESET_PASS_SUCCESS":
      return {
        ...state,
        isLoggedIn: false
      };

    case "API_INVITE_SUCCESS":
      return {
        ...state,
        isLoggedIn: true
      };

    case "API_WS_LOGIN_SUCCESS":
      return {
        ...state,
        isWsLoggedIn: true,
      };

    case "API_WS_LOGOUT":
      return {
        ...state,
        isWsLoggedIn: false,
      };

    default:
      return state;
  }
}
