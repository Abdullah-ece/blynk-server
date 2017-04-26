import {applyTransformers} from 'services/API';
import {transformTimeToTimestamp, transformShiftToMinutes} from 'services/Products';

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
