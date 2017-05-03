import {applyRequestTransformers, applyResponseTransformers} from 'services/API';
import {
  transformTimeToTimestamp,
  transformRangeToMinutes,
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

export function ProductUpdate(data) {
  return {
    type: 'API_PRODUCT_UPDATE',
    payload: {
      request: {
        method: 'post',
        url: '/product',
        data: data,
        transformRequest: applyRequestTransformers([transformTimeToTimestamp, transformRangeToMinutes])
      }
    }
  };
}

export function ProductDelete(id) {

  if (isNaN(Number(id)))
    throw Error('ProductDelete is missing id parameter');

  return {
    type: 'API_PRODUCT_DELETE',
    payload: {
      request: {
        method: 'delete',
        url: `/product/${id}`
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
        transformRequest: applyRequestTransformers([transformTimeToTimestamp, transformRangeToMinutes])
      }
    }
  };
}
