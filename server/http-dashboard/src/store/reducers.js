// Set up your root reducer here...
import {combineReducers} from 'redux';

// Redux-Form reducer
import {reducer as formReducer} from 'redux-form';

// There are in-app reducers we want to connect to store

const reducers = {
  form: formReducer
};

export default combineReducers(reducers);
