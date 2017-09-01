import {fromJS} from 'immutable';

const parseWidgetData = (data) => {
  if (!data) return [];

  return data.map((item) => {
    const key = Object.keys(item)[0];
    const value = item[key];

    return {
      x: Number(key),
      y: Number(value)
    };
  });
};

const initialState = fromJS({
  widgetsData: {
    /*
    deviceId: {
      widgetId: {
        pin: {
          data: Array
          loading: bool
        }
      }
    }
    */
  }
});

export default function Product(state = initialState, action) {
  switch (action.type) {

    case "API_WIDGETS_HISTORY_BY_PIN_REQUEST":

      return state.setIn(['widgetsData', action.value.deviceId, action.value.widgetId, action.value.pin], fromJS({
        loading: true,
        data: []
      }));

    case "API_WIDGETS_HISTORY_BY_PIN_SUCCESS":

      return state.setIn(['widgetsData', action.meta.previousAction.value.deviceId, action.meta.previousAction.value.widgetId, action.meta.previousAction.value.pin], fromJS(
        {
          loading: false,
          data: parseWidgetData(action.payload.data[0][action.meta.previousAction.value.pin].data)
        }
      ));

    case "API_WIDGETS_HISTORY_BY_PIN_FAIL":
      return state.setIn(['widgetsData', action.meta.previousAction.value.deviceId, action.meta.previousAction.value.widgetId, action.meta.previousAction.value.pin], fromJS(
        {
          loading: false,
          data: []
        }
      ));


    default:
      return state;
  }
}
