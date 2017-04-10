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

export function AccountResetPassword() {
  return {
    type: 'API_ACCOUNT_SAVE',
    payload: {
      request: {
        method: 'post',
        url: '/sendResetPass'
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
