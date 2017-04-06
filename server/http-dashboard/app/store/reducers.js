// Set up your root reducer here...
import {combineReducers} from 'redux';

// Redux-Form reducer
import {reducer as formReducer} from 'redux-form';

// There are in-app reducers we want to connect to store

import Login from '../data/Login/reducers';

const reducers = {
  form: formReducer,
  Login,
};

export default combineReducers(reducers);
