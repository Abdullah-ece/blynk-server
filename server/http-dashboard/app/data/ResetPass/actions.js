import {API_COMMANDS} from "store/blynk-websocket-middleware/commands";

export function SendResetPasswordEmail(params) {
  if(!params.email)
    throw new Error('SendResetEmail required argument is missed');

  return {
    type: 'SEND_RESET_PASSWORD',
    ws: {
      request: {
        command: API_COMMANDS.RESET_PASSWORD,
        query: [
          'start',
          params.email,
          'Blynk'
        ],
        waitForAuth: false,
      }
    }
  };
}

export function ResetPassword(params) {
  if(!params.token || !params.hash)
    throw new Error('SendPasswordChangeRequest required argument is missed');

  return {
    type: 'SEND_PASSWORD_CHANGE_REQUEST',
    ws: {
      request: {
        command: API_COMMANDS.RESET_PASSWORD,
        query: [
          'reset',
          params.token,
          params.hash,
        ],
        waitForAuth: false,
      }
    }
  };
}
