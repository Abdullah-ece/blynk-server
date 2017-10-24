import {API_URL} from 'services/API';

export function DevicesGet({orgId = null}) {

  if(!orgId) {
    throw new Error('orgId parameter is missed');
  }

  return {
    request: {
      method: 'get',
      url: API_URL.device().get({
        orgId: orgId
      })
    }
  };

}
