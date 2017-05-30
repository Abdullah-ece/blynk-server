import {Account as AccountFetch} from 'data/Account/actions';
import {OrganizationFetch} from 'data/Organization/actions';

import {StartLoading, FinishLoading} from 'data/PageLoading/actions';
import {bindActionCreators} from 'redux';

export const Organization = (store) => {


  return (nextState, replaceWith, callback) => {

    const start = bindActionCreators(StartLoading, store.dispatch);
    const finish = bindActionCreators(FinishLoading, store.dispatch);
    const accountFetch = bindActionCreators(AccountFetch, store.dispatch);
    const orgFetch = bindActionCreators(OrganizationFetch, store.dispatch);

    start();

    accountFetch().then(() => {
      const orgId = store.getState().Account.orgId;

      orgFetch({
        id: orgId
      }).then(() => {
        callback();
        finish();
      });
    });

  };
};
