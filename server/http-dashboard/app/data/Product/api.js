import {applyRequestTransformers, applyResponseTransformers} from 'services/API';
import {
  transformTimeToTimestamp,
  transformShiftToMinutes,
  transformTimestampToTime,
  transformMinutesToShift
} from 'services/Products';

export function ProductsFetch() {
  return {
    type: 'API_PRODUCTS_FETCH',
    payload: {
      request: {
        method: 'get',
        url: `/product`,
        transformResponse: applyResponseTransformers([transformTimestampToTime, transformMinutesToShift])
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
        transformRequest: applyRequestTransformers([transformTimeToTimestamp, transformShiftToMinutes])
      }
    }
  };
}
