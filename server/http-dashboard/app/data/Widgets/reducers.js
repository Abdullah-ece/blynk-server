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
      loading: bool,
      pin: {
        data: Array
      }
    }
    */
  }
});

export default function Product(state = initialState, action) {
  switch (action.type) {

    case "API_WIDGETS_HISTORY_BY_PIN":

      return action.value.pins.reduce((state, pin) => {
        return state.setIn(['widgetsData', action.value.deviceId, pin], fromJS(
          {
            data: []
          }
        ));
      }, state.setIn(['widgetsData', action.value.deviceId, 'loading'], true));

    case "API_WIDGETS_HISTORY_BY_PIN_SUCCESS":

      return action.meta.previousAction.value.pins.reduce((state, pin) => {
        return state.setIn(['widgetsData', action.meta.previousAction.value.deviceId, pin], fromJS(
          {
            data: parseWidgetData(action.payload.data[pin].data)
          }
        ));
      }, state.setIn(['widgetsData', action.meta.previousAction.value.deviceId, 'loading'], false));

    case "API_WIDGETS_HISTORY_BY_PIN_FAIL":
      return action.meta.previousAction.value.pins.reduce((state, pin) => {
        return state.setIn(['widgetsData', action.value.deviceId, pin], fromJS(
          {
            data: []
          }
        ));
      }, state.setIn(['widgetsData', action.meta.previousAction.value.deviceId, 'loading'], false));


    default:
      return state;
  }
}
