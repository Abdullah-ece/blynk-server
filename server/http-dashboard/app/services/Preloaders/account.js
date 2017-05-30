import {Account as AccountFetch} from 'data/Account/actions';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';
import {bindActionCreators} from 'redux';

export const Account = (store) => {
  return (nextState, replaceWith, callback) => {

    const start = bindActionCreators(StartLoading, store.dispatch);
    const finish = bindActionCreators(FinishLoading, store.dispatch);
    const fetch = bindActionCreators(AccountFetch, store.dispatch);

    start();

    fetch().then(() => {
      callback();
      finish();
    });

  };
};
