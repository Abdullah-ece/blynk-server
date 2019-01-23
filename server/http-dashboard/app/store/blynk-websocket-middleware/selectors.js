import { API_COMMANDS, COMMANDS } from "./commands";

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

export const getCommandKeyName = (value) => {
  for(let key in COMMANDS){
    if(COMMANDS[key] == value){
      return key;
    }
  }

  for(let key in API_COMMANDS){
    if(API_COMMANDS[key] == value){
      return key;
    }
  }

  return value;
};
