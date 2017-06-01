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
