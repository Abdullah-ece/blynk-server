import {transformJsonToFormUrlEncoded} from 'services/Form';

export function Login(data) {
  return {
    type: 'API_LOGIN',
    payload: {
      request: {
        transformRequest: transformJsonToFormUrlEncoded,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        method: 'post',
        url: '/login',
        data
      }
    }
  };
}
