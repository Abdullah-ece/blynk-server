import {createStore, compose, applyMiddleware} from 'redux';
import reduxImmutableStateInvariant from 'redux-immutable-state-invariant';
import thunk from 'redux-thunk';
import rootReducer from './reducers';
import axios from 'axios';
import axiosMiddleware from 'redux-axios-middleware';
import {persistStore, autoRehydrate} from 'redux-persist';

/* instance for basic API */
const axiosAPI = axios.create({
  baseUrl: 'http://localhost:8080/',
  responseType: 'json'
});

/* Persist Store Config for PROD & DEV */
const persisStoreConfig = {
  whitelist: [
    /*
     there is white list of stores we should store on storage like LocalStorage.
     Description for each of whitelists below:
     - Login - stores user token
     - Name - description
     */
    'Login'
  ]
};
/* Persist Store Config for DEV */
const persisStoreConfigDev = {};
/* Persist Store Config for PROD */
const persisStoreConfigProd = {};

function configureStoreProd(initialState) {
  const middlewares = [
    thunk,
    axiosMiddleware(axiosAPI)
  ];

  const store = createStore(rootReducer, initialState, compose(
    applyMiddleware(...middlewares),
    autoRehydrate(),
    )
  );

  return new Promise((resolve) => {
    persistStore(store, Object.assign({}, persisStoreConfig, persisStoreConfigProd), () => {
      resolve(store);
    });
  });
}

function configureStoreDev() {
  const middlewares = [
    reduxImmutableStateInvariant(),
    thunk,
    axiosMiddleware(axiosAPI)
  ];

  const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose; // add support for Redux dev tools
  const store = createStore(rootReducer, composeEnhancers(
    applyMiddleware(...middlewares),
    autoRehydrate()
    )
  );

  if (module.hot) {
    module.hot.accept('./reducers', () => {
      const nextReducer = require('./reducers').default; // eslint-disable-line global-require
      store.replaceReducer(nextReducer);
    });
  }

  return new Promise((resolve) => {
    persistStore(store, Object.assign({}, persisStoreConfig, persisStoreConfigDev), () => {
      resolve(store);
    });
  });

}

const configureStore = process.env.NODE_ENV === 'production' ? configureStoreProd : configureStoreDev;

export default configureStore;
