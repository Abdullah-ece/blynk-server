export function ForgotPass(data) {
  return {
    type: 'API_RESET',
    payload: {
      request: {
        method: 'post',
        url: '/account/resetPass',
        data
      }
    }
  };
}
