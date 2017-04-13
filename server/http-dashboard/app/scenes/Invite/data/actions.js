import {transformJsonToFormUrlEncoded} from 'services/Form';

export function Invite(data) {
  return {
    type: 'API_INVITE',
    payload: {
      request: {
        transformRequest: transformJsonToFormUrlEncoded,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        method: 'post',
        url: '/invite',
        data
      }
    }
  };
}
