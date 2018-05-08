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
  products: () => `/product`,
  organization: (params = {}) => `/organization${params.id ? `/${params.id}` : ''}`,
  device: () => ({
    get: (params) => `/devices/${params.orgId}${params.deviceId ? `/${params.deviceId}` : '' }`,
    update: (params) => `/devices/${params.orgId}`,
    metadata: () => ({
      update: (params) => `/devices/${params.orgId}/${params.deviceId}/updateMetaField`
    })
  }),
  ota: () => ({
    firmwareInto: (params) => `/ota/firmwareInfo?file=${params.firmwareUploadUrl || ''}`,
    start: () => `/ota/start`,
  }),
  widgets: () => ({
    history: (params) => {
      return `/data/${params.deviceId}/history`;
    },
    historyByPins: (params) => {
      const dataStreams = params.pins.map((pin) => `dataStream=${pin}`).join('&');
      return `/data/${params.deviceId}/history?${dataStreams}&limit=10000&offset=0&from=${params.from || 0}&to=${params.to || (new Date().getTime())}`;
    }
  })
};
