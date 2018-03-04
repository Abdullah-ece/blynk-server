import {fromJS} from 'immutable';
import {
  DEVICES_SORT
} from 'services/Devices';

import {ACTIONS} from 'store/blynk-websocket-middleware/actions';

const cutDeviceNameMetaFieldFromMetaFields = (device) => {
  if (!device.has('metaFields')) {
    return device;
  }

  device = device.update('metaFields',(metaFields)=>{
    return metaFields.filter(metaField=>{
      return metaField.get('name') !== 'Device Name';
    });
  });

  return device;
};

const initialState = fromJS({
  devicesLoading: false,
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

    case ACTIONS.BLYNK_WS_HARDWARE:
      return state.updateIn(['deviceDetails', 'info', 'data', 'webDashboard', 'widgets'], (widgets) => {
        return widgets.map((widget) => {
          return widget.update('sources', (sources) => sources.map((source) => {
            if(String(source.getIn(['dataStream', 'pin'])) === String(action.value.pin)) {
              return source.setIn(['dataStream', 'value'], action.value.value);
            }
            return source;
          }));
        });
      });

    case ACTIONS.BLYNK_WS_VIRTUAL_WRITE:

      return state.updateIn(['deviceDetails', 'info', 'data', 'webDashboard', 'widgets'], (widgets) => {
        return widgets.map((widget) => {
          return widget.update('sources', (sources) => sources.map((source) => {
            if(String(source.getIn(['dataStream', 'pin'])) === String(action.value.pin)) {
              return source.setIn(['dataStream', 'value'], action.value.value);
            }
            return source;
          }));
        });
      });

    case "API_DEVICES_FETCH":
      return state.set('devicesLoading', true);

    case "API_DEVICES_FAILURE":
      return state.set('devicesLoading', true);

    case "API_DEVICES_FETCH_SUCCESS":
      return state.set('devices', fromJS(action.payload.data))
        .set('devicesLoading', false);

    case "API_DEVICE_FETCH_SUCCESS":
      return state.update('devices', (devices) => {
        return devices.map((device) => (
          device.get('id') === action.payload.data.id ?
            fromJS(action.payload.data) : device
        ));
      });

    case "API_DEVICES_UPDATE_SUCCESS":
      return state.set('devices', fromJS(action.payload.data));

    case "API_DEVICES_DELETE_SUCCESS":
      return state;

    case "API_DEVICES_DELETE":
      return state;

    case "API_TIMELINE_FETCH_SUCCESS":
      return state.set('timeline', fromJS(action.payload.data));

    case "DEVICES_SORT_CHANGE":
      return state.setIn(['sorting', 'value'], action.value);

    case "API_DEVICE_DETAILS_FETCH_SUCCESS":
      return state.setIn(['deviceDetails', 'info', 'data'], cutDeviceNameMetaFieldFromMetaFields(fromJS(action.payload.data))).update('devices', (devices) => {
        return devices.map((device) => {

          if(Number(device.get('id')) !== Number(action.payload.data.id)) return device;

          const criticalSinceLastView = device.get('criticalSinceLastView') || 0;
          const warningSinceLastView = device.get('warningSinceLastView') || 0;

          return fromJS(action.payload.data)
            .set('criticalSinceLastView', criticalSinceLastView)
            .set('warningSinceLastView', warningSinceLastView);

        });
      });

    case "API_DEVICE_AVAILABLE_ORGANIZATIONS_FETCH_SUCCESS":
      return state.setIn(['deviceCreate', 'data'], fromJS(action.payload.data));

    case "DEVICES_DEVICE_DETAILS_UPDATE":
      return state.set('deviceDetails', action.value);

    case "API_DEVICE_DETAILS_UPDATE_SUCCESS":
      // this is call back fires when user updates
      // device name. update device name only to keep
      // critical & warning state
      return state.update('devices', (devices) => {
        return devices.map((device) => {

          if(Number(device.get('id')) !== Number(action.payload.data.id)) return device;

          const criticalSinceLastView = device.get('criticalSinceLastView') || 0;
          const warningSinceLastView = device.get('warningSinceLastView') || 0;

          return fromJS(action.payload.data)
            .set('criticalSinceLastView', criticalSinceLastView)
            .set('warningSinceLastView', warningSinceLastView);

        });
      });

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
