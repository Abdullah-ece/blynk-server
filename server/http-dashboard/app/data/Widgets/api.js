import {API_URL} from 'services/API';
import {
  DevicesGet
} from 'services/Actions/Devices';
import {
  DataHistoryGet,
} from 'services/Actions/Data';

export function WidgetHistoryByPinFetch(data) {

  /*
  */

  if (!data.deviceId)
    throw new Error('Missing device id parameter for widget HistoryByPin fetch');

  if (!data.pins)
    throw new Error('Missing pins parameter for widget HistoryByPin fetch');


  return {
    type: 'API_WIDGETS_HISTORY_BY_PIN',
    value: data,
    payload: {
      request: {
        method: 'get',
        url: API_URL.widgets().historyByPins(data)
      }
    }
  };
}

export function WidgetsHistory(data) {
  if (!data.deviceId)
    throw new Error('Missing device id parameter for widget history fetch');

  if (!data.dataQueryRequests)
    throw new Error('Missing dataQueryRequests parameter for widget history fetch');

  return {
    type: 'API_WIDGETS_HISTORY',
    value: data,
    payload: {
      request: {
        method: 'post',
        url: API_URL.widgets().history(data),
        data: data,
      }
    }
  };

}

export function WidgetProductsFetch(data) {

  return {
    type: 'API_WIDGETS_PRODUCTS_FETCH',
    value: data,
    payload: {
      request: {
        method: 'get',
        url: API_URL.products()
      }
    }
  };
}

export function WidgetOrganizationsFetch(data) {

  return {
    type: 'API_WIDGETS_ORGANIZATIONS_FETCH',
    value: data,
    payload: {
      request: {
        method: 'get',
        url: API_URL.organization()
      }
    }
  };
}

export function WidgetOrganizationFetch(data) {

  return {
    type: 'API_WIDGETS_ORGANIZATION_FETCH',
    value: data,
    payload: {
      request: {
        method: 'get',
        url: API_URL.organization({
          id: data.orgId
        })
      }
    }
  };
}

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
