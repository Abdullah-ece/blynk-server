export const DevicesFetch = () => {
  return {
    type: 'API_DEVICES_FETCH',
    payload: {
      request: {
        method: 'get',
        url: '/devices'
      }
    }
  };
};

export const DeviceFetch = (params) => {
  return {
    type: 'API_DEVICE_FETCH',

    payload: {
      request: {
        method: 'get',
        url: `/devices/${params.id}`
      }
    }
  };
};

export const DeviceUpdate = (device) => {
  return {
    type: 'API_DEVICE_UPDATE',
    payload: {
      request: {
        method: 'post',
        url: `/devices`,
        data: device
      }
    }
  };
};

export const DeviceCreate = (device) => {
  return {
    type: 'API_DEVICE_CREATE',
    payload: {
      request: {
        method: 'put',
        url: `/devices`,
        data: device
      }
    }
  };
};

export const DevicesUpdate = (devices) => {
  return {
    type: 'API_DEVICES_UPDATE',
    payload: {
      request: {
        method: 'post',
        url: `/devices`,
        data: devices
      }
    }
  };
};
