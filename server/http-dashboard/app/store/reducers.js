// Set up your root reducer here...
import {combineReducers} from 'redux';

// Redux-Form reducer
import {reducer as formReducer} from 'redux-form';

import reducer from './blynk-websocket-middleware/reducer';

// There are in-app reducers we want to connect to store

import Login from '../data/Login/reducers';
import Account from '../data/Account/reducers';
import Organization from '../data/Organization/reducers';
import Organizations from '../data/Organizations/reducers';
import Product from '../data/Product/reducers';
import Storage from '../data/Storage/reducers';
import PageLoading from '../data/PageLoading/reducers';
import Devices from '../data/Devices/reducers';
import Connection from '../data/Connection/reducers';
import RolesAndPermissions from '../data/RolesAndPermissions/reducers';
// import Widgets from '../data/Widgets/reducers';
import UserProfile from '../data/UserProfile/reducers';

// @todo refactor into one reducer
// this function is a hack to combine Widgets and Devices to one state
// because both store widgets data and their reducers

// function combineDevicesAndWidgets (state, action) {
//
//   const devicesState = Devices(state, action);
//
//   const widgetsState = Widgets(devicesState && devicesState.get('Widgets'), action, devicesState);
//
//   return fromJS({
//       ...devicesState.toJS(),
//     Widgets: widgetsState.toJS()
//   });
// }

const reducers = {
  form: formReducer,
  BlynkWS: reducer,
  Login,
  Account,
  Organization,
  Product,
  Storage,
  PageLoading,
  Devices: Devices,
  Organizations,
  UserProfile,
  Connection,
  RolesAndPermissions,
};

export default combineReducers(reducers);
