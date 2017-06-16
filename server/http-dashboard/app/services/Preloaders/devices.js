import {DevicesFetch, DeviceFetch} from 'data/Devices/api';
import {ProductsFetch} from 'data/Product/api';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';
import {bindActionCreators} from 'redux';

export const Devices = (store) => {
  return (nextState, replaceWith, callback) => {

    const start = bindActionCreators(StartLoading, store.dispatch);
    const finish = bindActionCreators(FinishLoading, store.dispatch);
    const devicesFetch = bindActionCreators(DevicesFetch, store.dispatch);
    const productsFetch = bindActionCreators(ProductsFetch, store.dispatch);

    start();

    devicesFetch().then(() => {
      productsFetch().then(() => {
        callback();
        finish();
      });
    });

  };
};

export const Device = (store) => {
  return (nextState, replaceWith, callback) => {

    const start = bindActionCreators(StartLoading, store.dispatch);
    const finish = bindActionCreators(FinishLoading, store.dispatch);
    const fetch = bindActionCreators(DeviceFetch, store.dispatch);

    start();

    fetch({
      id: nextState.params.id
    }).then(() => {
      callback();
      finish();
    });

  };
};

export const DeviceCreate = (store) => {
  return (nextState, replaceWith, callback) => {

    const start = bindActionCreators(StartLoading, store.dispatch);
    const finish = bindActionCreators(FinishLoading, store.dispatch);
    const devicesFetch = bindActionCreators(DevicesFetch, store.dispatch);
    const productsFetch = bindActionCreators(ProductsFetch, store.dispatch);

    start();

    devicesFetch().then(() => {
      productsFetch().then(() => {
        callback();
        finish();
      });
    });

  };
};

export const DeviceByIdCreate = (store) => {
  return (nextState, replaceWith, callback) => {

    const start = bindActionCreators(StartLoading, store.dispatch);
    const finish = bindActionCreators(FinishLoading, store.dispatch);
    const deviceFetch = bindActionCreators(DeviceFetch, store.dispatch);
    const productsFetch = bindActionCreators(ProductsFetch, store.dispatch);

    start();

    deviceFetch({
      id: nextState.params.id
    }).then(() => {
      productsFetch().then(() => {
        callback();
        finish();
      });
    });

  };
};
