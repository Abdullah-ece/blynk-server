// Set up your root reducer here...
import {combineReducers} from 'redux';

// Redux-Form reducer
import {reducer as formReducer} from 'redux-form';

// There are in-app reducers we want to connect to store

import Login from '../data/Login/reducers';
import Account from '../data/Account/reducers';
import Organization from '../data/Organization/reducers';
import Organizations from '../data/Organizations/reducers';
import Product from '../data/Product/reducers';
import Storage from '../data/Storage/reducers';
import PageLoading from '../data/PageLoading/reducers';
import Devices from '../data/Devices/reducers';
import Widgets from '../data/Widgets/reducers';
import UserProfile from '../data/UserProfile/reducers';

const reducers = {
  form: formReducer,
  Login,
  Account,
  Organization,
  Product,
  Storage,
  PageLoading,
  Devices,
  Organizations,
  Widgets,
  UserProfile,
};

export default combineReducers(reducers);
