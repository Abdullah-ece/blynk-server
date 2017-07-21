import {fromJS} from 'immutable';

const initialState = fromJS({
  list: null,
  manage: {
    activeTab: 'info', //hardcoded info tab key
    info: {
      form: ''
    },
    products: {
      form: ''
    },
    admins: {
      form: ''
    }
  }
});

export default function Organizations(state = initialState, action) {
  switch (action.type) {

    case "API_ORGANIZATIONS_FETCH_SUCCESS":
      return state.set('list', fromJS(action.payload.data));

    case "ORGANIZATIONS_MANAGE_SET_ACTIVE_TAB":
      return state.setIn(['manage', 'activeTab'], action.value);

    default:
      return state;
  }
}
