export const BASE_APP_URL = '/dashboard';
export const BASE_API_URL = '/api';
export const FILE_UPLOAD_URL = `${BASE_API_URL}/upload`;

export const applyRequestTransformers = (transformers) => {
  return (data) => {
    return transformers.reduce((data, transformer) => {
      return JSON.stringify(transformer(JSON.parse(data)));
    }, JSON.stringify(data));
  };
};

export const applyResponseTransformers = (transformers) => {
  return (data) => {
    return transformers.reduce((data, transformer) => {
      return transformer(data);
    }, JSON.parse(data));
  };
};

export const API_URL = {
  organization: () => `/organization`,
  device: () => ({
    update: (params) => `/devices/${params.orgId}`,
    metadata: () => ({
      update: (params) => `/devices/${params.orgId}/${params.deviceId}/updateMetaField`
    })
  }),
  widgets: () => ({
    historyByPin: (params) => `/data/${params.deviceId}/history?dataStream=${params.pin}&limit=10000&offset=0`
  })
};
