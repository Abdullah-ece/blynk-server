import {transformJsonToFormUrlEncoded} from 'services/Form';

export function Account() {
  return {
    type: 'API_ACCOUNT',
    payload: {
      request: {
        method: 'get',
        url: '/account'
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
    payload: {
      request: {
        method: 'post',
        url: '/account',
        data: data
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
