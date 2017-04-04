import reduceReducers from 'reduce-reducers';

import API from './data/reducers';

const initialState = {
  isLoggedIn: false
};

function Login(state = initialState) {
  return state;
}

export default reduceReducers(Login, API);
