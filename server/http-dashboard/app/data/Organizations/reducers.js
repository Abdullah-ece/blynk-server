import {fromJS} from 'immutable';

const initialState = fromJS({
  list: null
});

export default function Account(state = initialState, action) {
  switch (action.type) {

    case "API_ORGANIZATIONS_FETCH_SUCCESS":
      return state.set('list', fromJS(action.payload.data));

    default:
      return state;
  }
}
