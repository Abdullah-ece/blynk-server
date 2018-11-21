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

export function AccountSelectOrgId({ orgId }) {
  return {
    type: 'ACCOUNT_SAVE_CREDENTIALS',
    value: orgId
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
    ws: {
      request: {
        command: API_COMMANDS.RESET_PASSWORD,
        query: [
          'start',
          data.email,
          'Blynk'
        ],
        waitForAuth: false,
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
