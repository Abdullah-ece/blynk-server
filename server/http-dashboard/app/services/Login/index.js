import axios from 'axios';
import {BASE_API_URL} from 'services/API';
import {StorageRememberRequestedPage} from 'data/Storage/actions';

export const isLoggedIn = () => {
  return axios.get(`${BASE_API_URL}/account`);
};

export const RouteAuthorizedOnly = (store) => {
  return (nextState, replaceWith, callback) => {
    isLoggedIn().then(() => {
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

export const RouteGuestOnly = () => {
  return (nextState, replaceWith, callback) => {
    isLoggedIn().then(() => {
      replaceWith('/devices');
      callback();
    }).catch(() => {
      callback();
    });
  };
};


