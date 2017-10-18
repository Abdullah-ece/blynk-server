import {API_URL} from 'services/API';

export function DataHistoryGet(params = {}, data = {}) {

  const { deviceId } = params;

  if(!deviceId) {
    throw new Error('deviceId parameter is missed');
  }

  return {
    request: {
      method: 'post',
      url: API_URL.widgets().history({
        deviceId: deviceId
      }),
      data: data,
    }
  };

}
