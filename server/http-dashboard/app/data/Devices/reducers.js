import {fromJS} from 'immutable';

const initialState = fromJS({
  devices: [],
  timeline: {}
});

export default function Devices(state = initialState, action) {

  switch (action.type) {
    case "API_DEVICES_FETCH_SUCCESS":
      return state.set('devices', fromJS(action.payload.data));

    case "API_DEVICE_FETCH_SUCCESS":
      return state.update('devices', (devices) => {
        return devices.map((device) => (
          device.get('id') === action.payload.data.id ?
            fromJS(action.payload.data) : device
        ));
      });

    case "API_DEVICES_UPDATE_SUCCESS":
      return state.set('devices', fromJS(action.payload.data));

    case "API_TIMELINE_FETCH_SUCCESS":

      action.payload.data.logEvents.unshift({
        "id": 554,
        "deviceId": 1,
        "eventType": "CRITICAL",
        "ts": 1497290276606,
        "eventHashcode": 613812780,
        "description": "This is my description",
        "isResolved": true,
        "resolvedBy": "Vasya Pupkin",
        "name": "Temp is super high"
      });

      return state.set('timeline', fromJS(action.payload.data));

    case "API_DEVICE_UPDATE_SUCCESS":
      return state.update('devices', (devices) => {
        return devices.map((device) => (
          device.get('id') === action.payload.data.id ?
            fromJS(action.payload.data) : device
        ));
      });

    default:
      return state;
  }

}
