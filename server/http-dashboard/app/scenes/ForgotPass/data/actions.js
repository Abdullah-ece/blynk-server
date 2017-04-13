import {transformJsonToFormUrlEncoded} from 'services/Form';

export function ForgotPass(data) {
  return {
    type: 'API_SEND_RESET_PASS',
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
