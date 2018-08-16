import {transformJsonToFormUrlEncoded} from 'services/Form';
import {API_COMMANDS} from "store/blynk-websocket-middleware/commands";

export function Account() {
  return {
    type: 'API_ACCOUNT',
    ws: {
      request: {
        command: API_COMMANDS.GET_ACCOUNT
      }
    }
  };
}

export function AccountSaveCredentials({ username, password }) {
  return {
    type: 'ACCOUNT_SAVE_CREDENTIALS',
    value: {
      username: username,
      password: password
    }
  };
}

export function AccountClearCredentials() {
  return {
    type: 'ACCOUNT_CLEAR_CREDENTIALS',
  };
}

export function AccountResetPassword(data) {
  return {
    type: 'API_ACCOUNT_SEND_RESET_PASS',
    payload: {
      request: {
        transformRequest: transformJsonToFormUrlEncoded,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        method: 'post',
        url: '/sendResetPass',
        data
      }
    }
  };
}

export function AccountSave(data) {
  return {
    type: 'API_ACCOUNT_SAVE',
    ws: {
      request: {
        command: API_COMMANDS.UPDATE_ACCOUNT,
        query: [
          JSON.stringify(data)
        ],
      }
    }
  };
}

export function updateName(name) {
  return {
    type: 'ACCOUNT_UPDATE_NAME',
    name: name
  };
}
