import {DevicesFetch, DeviceFetch} from 'data/Devices/api';
import {ProductsFetch} from 'data/Product/api';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';
import {bindActionCreators} from 'redux';

const getOrgId = (store) => {
  return store.getState().Account.orgId;
};

const getActions = (store) => {
  return {
    start: bindActionCreators(StartLoading, store.dispatch),
    finish: bindActionCreators(FinishLoading, store.dispatch),
    devicesFetch: bindActionCreators(DevicesFetch, store.dispatch),
    deviceFetch: bindActionCreators(DeviceFetch, store.dispatch),
    productsFetch: bindActionCreators(ProductsFetch, store.dispatch),
  };
};

export const Devices = (store) => {
  return (nextState, replaceWith, callback) => {

    const {start, finish, devicesFetch, productsFetch} = getActions(store);

    start();

    devicesFetch({
      orgId: getOrgId(store)
    }).then(() => {
      productsFetch().then(() => {
        callback();
        finish();
      });
    });

  };
};

export const DeviceCreate = (store) => {
  return (nextState, replaceWith, callback) => {

    const {start, finish, devicesFetch, productsFetch} = getActions(store);

    start();

    devicesFetch({
      orgId: getOrgId(store)
    }).then(() => {
      productsFetch().then(() => {
        callback();
        finish();
      });
    });

  };
};
