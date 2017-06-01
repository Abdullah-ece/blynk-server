import {DevicesFetch} from 'data/Devices/api';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';
import {bindActionCreators} from 'redux';

export const Devices = (store) => {
  return (nextState, replaceWith, callback) => {

    const start = bindActionCreators(StartLoading, store.dispatch);
    const finish = bindActionCreators(FinishLoading, store.dispatch);
    const fetch = bindActionCreators(DevicesFetch, store.dispatch);

    start();

    fetch().then(() => {
      callback();
      finish();
    });

  };
};
