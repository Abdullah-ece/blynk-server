import {applyTransformers} from 'services/API';
import {transformTimeToTimestamp, transformShiftToMinutes} from 'services/Products';

export function ProductsFetch() {
  return {
    type: 'API_PRODUCTS_FETCH',
    payload: {
      request: {
        method: 'get',
        url: `/product`
      }
    }
  };
}

export function ProductCreate(data = false) {
  return {
    type: 'API_PRODUCT_CREATE',
    payload: {
      request: {
        method: 'put',
        url: '/product',
        data: data,
        transformRequest: applyTransformers([transformTimeToTimestamp, transformShiftToMinutes])
      }
    }
  };
}
