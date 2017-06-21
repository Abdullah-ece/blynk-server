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

export const TimelineFetch = (params = {}) => {

  if (!params.deviceId) {
    throw new Error('Required parameter deviceId is missed');
  }

  // default parameters

  params = Object.assign({}, {
    from: 0,
    to: new Date().getTime(),
    limit: 50,
    offset: 0
  }, params);

  return {
    type: 'API_TIMELINE_FETCH',
    payload: {
      request: {
        method: 'get',
        url: `/devices/${params.deviceId}/timeline`,
        params: params
      }
    }
  };
};

export const TimelineResolve = (params) => {

  if (!params.eventId || !params.deviceId)
    throw new Error('Required parameter is missed');

  return {
    type: 'API_TIMELINE_RESOLVE',
    payload: {
      request: {
        method: 'post',
        url: `/devices/${params.deviceId}/resolveEvent/${params.eventId}`,
        data: {
          comment: params.comment
        }
      }
    }
  };
};
