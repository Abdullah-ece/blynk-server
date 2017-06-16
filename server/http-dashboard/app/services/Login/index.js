import axios from 'axios';

export const isLoggedIn = () => {
  return axios.get('/dashboard/account');
};

export const RouteAuthorizedOnly = () => {
  return (nextState, replaceWith, callback) => {
    isLoggedIn().then(() => {
      callback();
    }).catch(() => {
      replaceWith('/login');
      callback();
    });
  };
};

export const RouteGuestOnly = () => {
  return (nextState, replaceWith, callback) => {
    isLoggedIn().then(() => {
      replaceWith('/products');
      callback();
    }).catch(() => {
      callback();
    });
  };
};


