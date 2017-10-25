import axios from 'axios';
import {BASE_API_URL} from 'services/API';

export const isLoggedIn = () => {
  return axios.get(`${BASE_API_URL}/account`);
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
      replaceWith('/devices');
      callback();
    }).catch(() => {
      callback();
    });
  };
};


