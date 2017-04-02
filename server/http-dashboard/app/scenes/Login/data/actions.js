export function Login(data) {
  return {
    type: 'API_LOGIN',
    payload: {
      request: {
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        method: 'post',
        url: '/login',
        data: data
      }
    }
  };
}
