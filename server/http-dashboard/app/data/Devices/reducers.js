import {fromJS} from 'immutable';
import {
  DEVICES_SORT
} from 'services/Devices';

const initialState = fromJS({
  devices: [],
  timeline: {},
  sorting: {
    value: DEVICES_SORT.REQUIRE_ATTENTION.key
  },
  deviceDetails: {
    info: {
      loading: true,
      data: null
    },
    timeline: {
      loading: true,
      data: null
    },
    dashboard: {},
    labels: {}
  },
  deviceCreate: {
    organizationLoading: false,
    data: null
  }
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
      return state.set('timeline', fromJS(action.payload.data));

    case "DEVICES_SORT_CHANGE":
      return state.setIn(['sorting', 'value'], action.value);

    case "API_DEVICE_DETAILS_FETCH_SUCCESS":
      return state.setIn(['deviceDetails', 'info', 'data'], fromJS(action.payload.data));

    case "API_DEVICE_AVAILABLE_ORGANIZATIONS_FETCH_SUCCESS":
      return state.setIn(['deviceCreate', 'data'], fromJS(action.payload.data));

    case "DEVICES_DEVICE_DETAILS_UPDATE":
      return state.set('deviceDetails', action.value);

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
