import {fromJS} from 'immutable';
import {
  DEVICES_SORT,
  // TIMELINE_TIME_FILTERS,
  DEVICES_FILTERS
} from 'services/Devices';
import {
  hardcodedRequiredMetadataFieldsNames
} from 'services/Products';


import {ACTIONS} from 'store/blynk-websocket-middleware/actions';

const cutDeviceNameMetaFieldFromMetaFields = (device) => {
  if (!device.metaFields) {
    return device;
  }

  const metaFields = device.metaFields.filter((metaField) => {
    return metaField.name !== 'Device Name';
  });

  return {
    ...device,
    metaFields: metaFields
  };
};

const getLocationMetaFieldOnly = (metaFields) => {
  if(!metaFields || !metaFields.length)
    return [];

  return metaFields.map((field) => (
    field && field.name && String(field.name).trim() === hardcodedRequiredMetadataFieldsNames.LocationName
  ));
};

const getFieldsForDevicesList = (device) => ({
  id: Number(device.id),
  name: device.name,
  productName: device.productName,
  criticalSinceLastView: device.criticalSinceLastView,
  warningSinceLastView: device.warningSinceLastView,
  //get location metafield to be able group devices list by location
  metaFields: getLocationMetaFieldOnly(device.metaFields),
  createdAt: device.createdAt,
  dataReceivedAt: device.dataReceivedAt,
});

const initialState = {

  devices: [],

  devicesForSearch: [],

  devicesListFilterValue: DEVICES_FILTERS.DEFAULT,

  deviceCreationModal: {
    organizations: [],
    organizationsLoading: false
  },

  devicesListSorting: {
    value: DEVICES_SORT.REQUIRE_ATTENTION.key
  },

  devicesWidgetsData: {},

  deviceDetails: {},

  deviceDashboardLiveData: {},
  deviceDashboardData: {},
  deviceDashboard: {},
  deviceDashboardLoading: true,

  // devicesLoading: false,
  // devices: [],
  // timeline: {},
  // sorting: {
  //   value: DEVICES_SORT.REQUIRE_ATTENTION.key
  // },
  // deviceDetails: {
  //   info: {
  //     loading: true,
  //     data: null
  //   },
  //   timeline: {
  //     loading: true,
  //     data: null
  //   },
  //   dashboard: {},
  //   labels: {}
  // },
  // deviceCreate: {
  //   organizationLoading: false,
  //   data: null
  // },
  // timeFilter: TIMELINE_TIME_FILTERS.LIVE.key,
};


function updateDeviceDetailsWidgets(state, action) {

  let {pin, deviceId, value} = action.value;

  pin = Number(pin);
  deviceId = Number(deviceId);
  value = String(value);

  let devicesWidgetsData = {...state.devicesWidgetsData};

  if (!devicesWidgetsData[deviceId])
    devicesWidgetsData[deviceId] = {};

  if (!devicesWidgetsData[deviceId][pin])
    devicesWidgetsData[deviceId][pin] = value;

  devicesWidgetsData[deviceId] = {
    ...devicesWidgetsData[deviceId],
    [pin]: value
  };

  return {
    ...state,
    devicesWidgetsData
  };

  // let deviceId = state.getIn(['deviceDetails', 'info', 'data', 'id']);
  //
  // if(Number(action.value.deviceId) !== Number(deviceId))
  //   return state;
  //
  // return state.updateIn(['deviceDetails', 'info', 'data', 'webDashboard', 'widgets'], (widgets) => {
  //   return widgets.map((widget) => {
  //     // do not update dataStream of linear and bar chart
  //     if([WIDGET_TYPES.LINEAR, WIDGET_TYPES.BAR].indexOf(widget.get('type'))>=0)
  //       return widget;
  //     return widget.update('sources', (sources) => sources.map((source) => {
  //       if(String(source.getIn(['dataStream', 'pin'])) === String(action.value.pin)) {
  //         return source.setIn(['dataStream', 'value'], action.value.value);
  //       }
  //       return source;
  //     }));
  //   });
  // });
}

export default function Devices(state = initialState, action) {

  switch (action.type) {

    case "DEVICES_TIME_FILTER_UPDATE":
      return state.set('timeFilter', action.value);

    case "DEVICES_LIST_FILTER_VALUE_CHANGE":
      return {
        ...state,
        devicesListFilterValue: action.value,
      };

    case ACTIONS.BLYNK_WS_HARDWARE:
      return updateDeviceDetailsWidgets(state, action);

    case ACTIONS.BLYNK_WS_VIRTUAL_WRITE:
      return updateDeviceDetailsWidgets(state, action);

    case "API_DEVICE_DASHBOARD_FETCH":
      return {
        ...state,
        deviceDashboardLiveData: {},
        deviceDashboardData: {},
        deviceDashboard: {},
        deviceDashboardLoading: true,
      };

    case "API_DEVICE_DASHBOARD_FETCH_SUCCESS":

      const dashboard = {
        ...action.payload.data && action.payload.data.webDashboard,
        widgets: action.payload.data.webDashboard.widgets || []
      };

      const sources = [];

      if (dashboard && dashboard.widgets && dashboard.widgets.length) {

        dashboard.widgets.map((widget) => {

          let dataStream = null;
          if (widget && widget.sources && widget.sources[0] && !isNaN(Number(widget.sources[0].dataStream.pin))) {
            dataStream = widget.sources[0].dataStream;
          }

          if (dataStream)
            sources.push({
              widgetId: widget.id,
              pin     : dataStream.pin,
              value   : dataStream.value
            });
        });

      }

      const deviceDashboardData = {};
      const deviceDashboardLiveData = {};

      sources.forEach((source) => {
        deviceDashboardData[source.widgetId] = {
          pin  : source.pin,
          value: source.value
        };
        deviceDashboardLiveData[source.pin] = source.value;
      });

      return {
        ...state,
        deviceDashboardLiveData: deviceDashboardLiveData,
        deviceDashboardData    : deviceDashboardData,
        deviceDashboard        : dashboard,
        deviceDashboardLoading : false
      };

    case "API_DASHBOARD_FETCH_FAILURE":
      return {
        deviceDashboard: [],
        deviceDashboardLoading: false
      };

    case "API_DEVICES_FETCH":
      return {
        ...state,
        devicesLoading: true
      };

    case "API_DEVICES_FAILURE":
      return {
        ...state,
        devicesLoading: false
      };

    case "API_DEVICES_FETCH_SUCCESS":

      return {
        ...state,
        devices: action.payload.data.map((device) => getFieldsForDevicesList(device)),
        // save full devices for smart search
        devicesForSearch: action.payload.data,
        devicesLoading: false
      };

    case "API_DEVICE_FETCH_SUCCESS":
      const devices = [...state.devices].map((device) => {
        if(device.id === action.payload.data.id)
          return action.payload.data;

        return device;
      });

      return {
        ...state,
        // @todo add simplify to devices
        devices: devices,
        devicesForSearch: devices,
      };

    case "API_DEVICES_UPDATE_SUCCESS":
      return state.set('devices', fromJS(action.payload.data));

    case "API_DEVICES_DELETE_SUCCESS":
      return state;

    case "API_DEVICES_DELETE":
      return state;

    case "API_TIMELINE_FETCH_SUCCESS":
      return state.set('timeline', fromJS(action.payload.data));

    case "DEVICES_SORT_CHANGE":
      return {
        ...state,
        devicesListSorting: {
          ...state.devicesListSorting,
          value: action.value
        }
      };

    case "API_DEVICE_DETAILS_FETCH_SUCCESS":

      // let devicesList = state.devices.map((device) => {
      //   if(device.id !== action.payload.data.id) return device;
      //
      //   const criticalSinceLastView = device.criticalSinceLastView || 0;
      //   const warningSinceLastView = device.warningSinceLastView || 0;
      //
      //   return {
      //     ...device,
      //     criticalSinceLastView: device.get('criticalSinceLastView') || 0,
      //     warningSinceLastView: device.get('warningSinceLastView') || 0,
      //   };
      // });

      return {
        ...state,
        deviceDetails: {
          ...cutDeviceNameMetaFieldFromMetaFields(action.payload.data)
        }
      };

      // return state.setIn(['deviceDetails', 'info', 'data'], cutDeviceNameMetaFieldFromMetaFields(fromJS(action.payload.data))).update('devices', (devices) => {
      //   return devices.map((device) => {
      //
      //     if(Number(device.get('id')) !== Number(action.payload.data.id)) return device;
      //
      //     const criticalSinceLastView = device.get('criticalSinceLastView') || 0;
      //     const warningSinceLastView = device.get('warningSinceLastView') || 0;
      //
      //     return fromJS(action.payload.data)
      //       .set('criticalSinceLastView', criticalSinceLastView)
      //       .set('warningSinceLastView', warningSinceLastView);
      //
      //   });
      // });

    case "API_DEVICE_AVAILABLE_ORGANIZATIONS_FETCH":
      return {
        ...state,
        deviceCreationModal: {
          ...state.deviceCreationModal,
          organizationsLoading: true
        }
      };

    case "API_DEVICE_AVAILABLE_ORGANIZATIONS_FETCH_SUCCESS":
      return {
        ...state,
        deviceCreationModal: {
          ...state.deviceCreationModal,
          organizations: action.payload.data,
          organizationsLoading: false
        }
      };

    case "API_DEVICE_AVAILABLE_ORGANIZATIONS_FETCH_FAILURE":
      return {
        ...state,
        deviceCreationModal: {
          ...state.deviceCreationModal,
          organizationsLoading: false
        }
      };

    case "DEVICES_DEVICE_DETAILS_UPDATE":
      return state.set('deviceDetails', action.value);

    case "API_DEVICE_DETAILS_UPDATE_SUCCESS":
      // this is call back fires when user updates
      // device name. update device name only to keep
      // critical & warning state

      return {
        ...state,
        deviceDetails: {
          ...state.deviceDetails,
          name: action.payload.data.name
        }
      };

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
