import {ProductsFetch} from 'data/Product/api';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';
import {bindActionCreators} from 'redux';

export const Products = (store) => {
  return (nextState, replaceWith, callback) => {

    const start = bindActionCreators(StartLoading, store.dispatch);
    const finish = bindActionCreators(FinishLoading, store.dispatch);
    const fetch = bindActionCreators(ProductsFetch, store.dispatch);

    start();

    fetch().then(() => {
      callback();
      finish();
    });

  };
};
