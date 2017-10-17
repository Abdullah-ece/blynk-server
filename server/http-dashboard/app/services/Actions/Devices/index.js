import {API_URL} from 'services/API';

export function DevicesGet({productId = null}) {

  if(!productId) {
    throw new Error('productId parameter is missed');
  }

  return {
    request: {
      method: 'get',
      url: API_URL.device().get({
        productId: productId
      })
    }
  };

}
