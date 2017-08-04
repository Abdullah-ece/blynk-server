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

export function ProductFetch(data) {
  if (!data.id)
    throw new Error('Missing id parameter for product fetch');

  return {
    type: 'API_PRODUCT_FETCH',
    payload: {
      request: {
        method: 'get',
        url: `/product/${data.id}`
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
        data: data
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
        data: data
      }
    }
  };
}

export function ProductUpdateDevices(data = false) {
  return {
    type: 'API_PRODUCT_UPDATE_DEVICES',
    payload: {
      request: {
        method: 'POST',
        url: '/product/updateDevices',
        data: data
      }
    }
  };
}

export function CanDeleteProduct(data = {}) {
  return {
    type: 'API_PRODUCT_CAN_DELETE_PRODUCT',
    payload: {
      request: {
        method: 'GET',
        url: `/product/canDeleteProduct/${data.id}`
      }
    }
  };
}

