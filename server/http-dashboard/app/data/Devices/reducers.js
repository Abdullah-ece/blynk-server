import {fromJS} from 'immutable';

const initialState = fromJS({
  devices: []
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
