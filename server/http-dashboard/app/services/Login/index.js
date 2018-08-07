import {StorageRememberRequestedPage} from 'data/Storage/actions';

export const isLoggedIn = (store) => {
  return new Promise((resolve, reject) => {
    if(store.getState().Login.isWsLoggedIn) {
      resolve();
    }
    reject();
  });
};

export const RouteAuthorizedOnly = (store) => {
  return (nextState, replaceWith, callback) => {
    isLoggedIn(store).then(() => {
      if(store.getState().Storage.requestedPage !== "/login") {
        replaceWith(store.getState().Storage.requestedPage);
        store.dispatch(StorageRememberRequestedPage("/login"));
      }
      callback();
    }).catch(() => {
      store.dispatch(StorageRememberRequestedPage(nextState.location.pathname));
      replaceWith('/login');
      callback();
    });
  };
};

export const RouteGuestOnly = (store) => {
  return (nextState, replaceWith, callback) => {
    isLoggedIn(store).then(() => {
      replaceWith('/devices');
      callback();
    }).catch(() => {
      callback();
    });
  };
};


