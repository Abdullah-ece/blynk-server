export function Logout() {
  return {
    type: 'API_LOGOUT',
    payload: {
      request: {
        method: 'post',
        url: '/logout',
      }
    }
  };
}
