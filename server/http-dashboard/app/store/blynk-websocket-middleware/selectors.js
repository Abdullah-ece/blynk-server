const ReducerName = 'BlynkWS';

export const getTrackDeviceId = (state) => {

  try {
    return state[ReducerName].get('trackDeviceId');
  } catch (e) {
    return null;
  }

};

export const getTrackOnlyByDeviceId = (state) => {
  try {
    return state[ReducerName].get('trackOnlyByDeviceId');
  } catch (e) {
    return null;
  }
};
