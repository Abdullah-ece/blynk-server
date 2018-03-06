import {fromJS} from 'immutable';
import {ACTIONS} from 'store/blynk-websocket-middleware/actions';
import {WIDGET_TYPES} from "services/Widgets";
import {
  TIMELINE_TIME_FILTERS
} from 'services/Devices';


const parseLineWidgetData = (response) => {

  return response.data.map((item) => {
    const key = item.key;
    const value = item.value;

    return {
      x: Number(key),
      y: Number(value)
    };
  });
};

const parseBarWidgetData = (response) => {

  return Object.keys(response.data).map((key) => {
    const value = response.data[key];

    return {
      name: String(key),
      value: Number(value)
    };
  });
};

const parseHistoryData = (response) => {

  if (!response.data) return [];

  if (Array.isArray(response.data)) {
    // parse line chart data
    return parseLineWidgetData(response);
  } else {
    // parse bar chart data
    return parseBarWidgetData(response);
  }

};

const filterDevicesByProductId = (list, productId) => {

  if(!productId)
    throw new Error('productId parameter is missed');

  return list.filter((item) => {
    return parseInt(item.get('productId')) === parseInt(productId);
  });

};

const initialState = fromJS({
  widgetsData: {
    /*
    ===NEW===
    deviceId: {
      loading: bool,
      widgetId: {
        sourceIndex: {
          pin: value
          data: Array
        }
      }
    }
    */
  },

  settingsModal: {
    previewAvailableDevices: {
      loading: false,
      list: null,
    },
    previewData: {
     // widgetId: {
     //   loading: bool,
     //   data: Array
     // }
    }
  }
});

export default function Product(state = initialState, action, DevicesState) {
  switch (action.type) {

    case ACTIONS.BLYNK_WS_VIRTUAL_WRITE:
      return state.updateIn(['widgetsData', String(action.value.deviceId)], (device) => device.map((widget) => {
        if(widget.map) {

          if(DevicesState.get('timeFilter') !== TIMELINE_TIME_FILTERS.LIVE.key)
            return widget;

          return widget.map((source) => {
            if(String(source.get('pin')) === String(action.value.pin)) {
              return source.update('data', (data) => data.push(fromJS({
                x: new Date().getTime(),
                y: Number(action.value.value)
              })));
            }
            return source;
          });
        }
        return widget;
      }));

    case ACTIONS.BLYNK_WS_HARDWARE:
      return state.updateIn(['widgetsData', String(action.value.deviceId)], (device) => device.map((widget) => {
        if(widget.map) {

          if([WIDGET_TYPES.LINEAR, WIDGET_TYPES.BAR].indexOf(widget.get('type'))>=0 && DevicesState.get('timeFilter') !== TIMELINE_TIME_FILTERS.LIVE.key)
            return widget;

          return widget.map((source) => {
            if(String(source.get('pin')) === String(action.value.pin)) {
              return source.update('data', (data) => data.push(fromJS({
                x: new Date().getTime(),
                y: Number(action.value.value)
              })));
            }
            return source;
          });
        }
        return widget;
      }));


    case "API_WIDGETS_HISTORY":

      return action.value.dataQueryRequests.reduce((state, request) => {
        return state.setIn(['widgetsData', String(action.value.deviceId), String(request.widgetId), String(request.sourceIndex)], fromJS({
          pin: String(request.pin),
          data: []
        }));
      }, state).setIn(['widgetsData', String(action.value.deviceId), 'loading'], true);

    case "API_WIDGETS_HISTORY_SUCCESS":

      return action.meta.previousAction.value.dataQueryRequests.reduce((state, request, key) => {
        return state.setIn(['widgetsData', String(action.meta.previousAction.value.deviceId), String(request.widgetId), String(request.sourceIndex)], fromJS(
          {
            pin: String(request.pin),
            data: parseHistoryData(action.payload.data[key])
          }
        ));
      }, state).setIn(['widgetsData', String(action.meta.previousAction.value.deviceId), 'loading'], false);

    case "API_WIDGETS_HISTORY_BY_PIN_SUCCESS":

      return state;

    case "API_WIDGETS_HISTORY_BY_PIN_FAIL":
      return action.meta.previousAction.value.pins.reduce((state, pin) => {
        return state.setIn(['widgetsData', action.value.deviceId, pin], fromJS(
          {
            data: []
          }
        ));
      }, state).setIn(['widgetsData', action.meta.previousAction.value.deviceId, 'loading'], false);


    case "API_WIDGET_DEVICES_PREVIEW_LIST_FETCH":
      return state.setIn(['settingsModal', 'previewAvailableDevices', 'loading'], true);

    case "API_WIDGET_DEVICES_PREVIEW_LIST_FETCH_SUCCESS":
      return state.updateIn(['settingsModal', 'previewAvailableDevices'], (data) => ( data.set('loading', false).set('list', filterDevicesByProductId(fromJS(action.payload.data), action.meta.previousAction.value.productId))));

    case "API_WIDGET_DEVICES_PREVIEW_LIST_FETCH_FAIL":
      return state.setIn(['settingsModal', 'previewAvailableDevices', 'loading'], false);

    case "API_WIDGET_DEVICES_PREVIEW_HISTORY_FETCH":
      return state.setIn(['settingsModal', 'previewData', action.value.widgetId], fromJS({ loading: true, data: [] }));

    case "API_WIDGET_DEVICES_PREVIEW_HISTORY_FETCH_SUCCESS":
      return state.updateIn(['settingsModal', 'previewData', action.meta.previousAction.value.widgetId], (previewData) => previewData.set('loading', false).set('data', fromJS(parseHistoryData(action.payload.data[0]))));

    case "API_WIDGET_DEVICES_PREVIEW_HISTORY_FETCH_FAIL":
      return state.setIn(['settingsModal', 'previewData', action.meta.previousAction.value.widgetId, 'loading'], false);

    case "WIDGET_DEVICES_PREVIEW_HISTORY_CLEAR":
      return state.setIn(['settingsModal', 'previewData'], fromJS({}));

    case "WIDGET_DEVICES_PREVIEW_LIST_CLEAR":
      return state.setIn(['settingsModal', 'previewAvailableDevices'], fromJS({
        loading: false,
        list: null,
      }));

    default:
      return state;
  }
}
