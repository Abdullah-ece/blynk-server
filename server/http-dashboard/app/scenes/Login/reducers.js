import reduceReducers from 'reduce-reducers';

import API from './data/reducers';

const initialState = {
  token: ''
};

function Login(state = initialState) {
  return state;
}

export default reduceReducers(Login, API);
