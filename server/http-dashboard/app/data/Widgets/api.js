import {
  DevicesGet
} from 'services/Actions/Devices';
import {
  DataHistoryGet,
} from 'services/Actions/Data';

export function WidgetDevicesPreviewListFetch(data = { productId: null }) {
  return {
    type: 'API_WIDGET_DEVICES_PREVIEW_LIST_FETCH',
    value: data,
    payload: DevicesGet(data)
  };
}

export function WidgetDevicesPreviewHistoryFetch(params, data = { }) {
  return {
    type: 'API_WIDGET_DEVICES_PREVIEW_HISTORY_FETCH',
    value: {
      widgetId: params.widgetId,
    },
    payload: DataHistoryGet(params, data)
  };
}
