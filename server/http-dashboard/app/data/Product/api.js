import {API_URL} from "services/API";

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

export function DevicesListForProductDashboardPreviewFetch(params = {}) {

  if(!params.orgId)
    throw new Error('Missing orgId parameter for DevicesListForProductDashboardPreviewFetch');

  if(!params.productId)
    throw new Error('Missing productId parameter for DevicesListForProductDashboardPreviewFetch');

  return {
    type: 'API_DEVICES_LIST_FOR_PRODUCT_DASHBOARD_PREVIEW_FETCH',
    productId: params.productId,
    payload: {
      request: {
        method: 'get',
        url: API_URL.device().get(params)
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

export function ProductInfoDevicesOTAFetch({ orgId }) {
  return {
    type: 'PRODUCT_INFO_DEVICES_OTA_FETCH',
    payload: {
      request: {
        method: 'GET',
        url: API_URL.device().get({ orgId })
      }
    }
  };
}

export function ProductInfoDevicesOTAStart({ productId, pathToFirmware, deviceIds, title }) {
  return {
    type: 'PRODUCT_INFO_DEVICES_OTA_START',
    payload: {
      request: {
        method: 'POST',
        url: API_URL.ota().start(),
        data: {
          productId,
          pathToFirmware,
          deviceIds,
          title
        }
      }
    }
  };
}

export function ProductInfoDevicesOTAFirmwareInfoFetch({ firmwareUploadUrl }) {

  return {
    type: 'PRODUCT_INFO_DEVICES_OTA_FIRMWARE_INFO_FETCH',
    payload: {
      request: {
        method: 'GET',
        url: API_URL.ota().firmwareInto({ firmwareUploadUrl })
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

