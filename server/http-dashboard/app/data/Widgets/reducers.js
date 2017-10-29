import {fromJS} from 'immutable';

const parseLineWidgetData = (response) => {

  return response.data.map((item) => {
    const key = Object.keys(item)[0];
    const value = item[key];

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

export default function Product(state = initialState, action) {
  switch (action.type) {

    case "API_WIDGETS_HISTORY":
      // return state;
      return action.value.dataQueryRequests.reduce((state, request) => {
        return state.setIn(['widgetsData', String(action.value.deviceId), String(request.widgetId), String(request.sourceIndex)], fromJS({
          data: []
        }));
      }, state.setIn(['widgetsData', String(action.value.deviceId), 'loading'], true));

    case "API_WIDGETS_HISTORY_SUCCESS":

      return action.meta.previousAction.value.dataQueryRequests.reduce((state, request, key) => {
        return state.setIn(['widgetsData', String(action.meta.previousAction.value.deviceId), String(request.widgetId), String(request.sourceIndex)], fromJS(
          {
            data: parseHistoryData(action.payload.data[key])
          }
        ));
      }, state.setIn(['widgetsData', action.meta.previousAction.value.deviceId, 'loading'], false));

    case "API_WIDGETS_HISTORY_BY_PIN_SUCCESS":

      return state;

    case "API_WIDGETS_HISTORY_BY_PIN_FAIL":
      return action.meta.previousAction.value.pins.reduce((state, pin) => {
        return state.setIn(['widgetsData', action.value.deviceId, pin], fromJS(
          {
            data: []
          }
        ));
      }, state.setIn(['widgetsData', action.meta.previousAction.value.deviceId, 'loading'], false));


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
