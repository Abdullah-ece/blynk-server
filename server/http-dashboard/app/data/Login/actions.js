import {API_COMMANDS} from "store/blynk-websocket-middleware/commands";

export function LoginWsSuccess() {
  return {
    type: 'API_WS_LOGIN_SUCCESS',
  };
}

export function LoginWsLogout() {
  return {
    type: 'API_WS_LOGOUT',
    ws  : {
      request: {
        command: API_COMMANDS.LOGOUT
      }
    }
  };
}

