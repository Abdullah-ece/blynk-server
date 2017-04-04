export default function Login(state = {}, action) {

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
    default:
      return state;
  }

}
