import {API_COMMANDS} from "store/blynk-websocket-middleware/commands";

export const PreloadDevicesFetch = (data) => {

  if (!data.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_PRELOAD_DEVICES_FETCH',
    ws: {
      request: {
        command: API_COMMANDS.GET_DEVICES,
        query: [data.orgId]
      }
    }
  };
};

export const DevicesFetch = (data) => {

  if (!data.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICES_FETCH',
    ws: {
      request: {
        command: API_COMMANDS.GET_DEVICES,
        query: [data.orgId]
      }
    }
  };
};

export const DeviceProductsFetch = () => {

  return {
    type: 'API_DEVICE_PRODUCTS_FETCH',
    ws: {
      request: {
        command: API_COMMANDS.GET_PRODUCTS
      }
    }
  };
};

export const DeviceFetch = (params, data) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_FETCH',

    ws: {
      request: {
        command: API_COMMANDS.GET_DEVICE,
        query: [params.orgId, data.id],
      }
    }
  };
};

export const DeviceCreate = (data, device) => {

  if (!data.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_CREATE',
    ws: {
      request: {
        command: API_COMMANDS.CREATE_DEVICE,
        query: [data.orgId, JSON.stringify(device)],
      }
    }
  };
};

export const DeviceDelete = (deviceId, orgId) => {

  if (!orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_DELETE',
    payload: {
      request: {
        method: 'delete',
        url: `/devices/${orgId}/${deviceId}`
      }
    }
  };
};

export const TimelineFetch = (params = {}) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

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
    ws: {
      request: {
        command: API_COMMANDS.GET_DEVICE_TIMELINE,
        query: [
          JSON.stringify(params)
        ],
      }
    }
  };
};

export const TimelineResolve = (params) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  if (!params.eventId || !params.deviceId)
    throw new Error('Required parameter is missed');

  return {
    type: 'API_TIMELINE_RESOLVE',
    ws: {
      request: {
        command: API_COMMANDS.LOG_EVENT_RESOLVE,
        query: [
          params.deviceId,
          params.eventId,
          params.comment,
        ],
      }
    }
  };
};

export const DeviceDetailsFetch = (params, data) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_DETAILS_FETCH',
    ws: {
      request: {
        command: API_COMMANDS.GET_DEVICE,
        query: [params.orgId,data.id]
      }
    }
  };
};

export const DeviceDetailsUpdate = (params, data) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  return {
    type: 'API_DEVICE_DETAILS_UPDATE',
    ws: {
      request: {
        command: API_COMMANDS.UPDATE_DEVICE,
        query: [
          params.orgId,
          JSON.stringify(data)
        ],
      }
    }
  };
};

export const DeviceDashboardFetch = ({orgId, deviceId }) => {

  if(!orgId)
    throw new Error('Parameter orgId is missed');

  return {
    type: 'API_DEVICE_DASHBOARD_FETCH',
    ws: {
      request: {
        command: API_COMMANDS.GET_DEVICE,
        query  : [orgId, deviceId],
      }
    }
  };
};

export const DeviceAvailableOrganizationsFetch = () => {

  return {
    type: 'API_DEVICE_AVAILABLE_ORGANIZATIONS_FETCH',

    ws: {
      request: {
        command: API_COMMANDS.GET_ORGS,
      }
    }
  };
};

export const DeviceMetadataUpdate = (params, metadata) => {

  if (!params.orgId)
    throw new Error('orgId parameter is missed');

  if (!params.deviceId)
    throw new Error('deviceId parameter is missed');

  return {
    type: 'API_DEVICE_METADATA_UPDATE',

    ws: {
      request: {
        command: API_COMMANDS.UPDATE_DEVICE_METAFIELD,
        query: [
          params.deviceId,
          JSON.stringify(metadata)
        ]
      }
    }
  };
};
