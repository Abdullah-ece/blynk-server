import {fromJS} from 'immutable';

const initialState = fromJS({
  list: null,
  details: {
    activeTab: 'info' //hardcoded info tab key
  },
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

    case "ORGANIZATIONS_MANAGE_UPDATE":
      return state.set('manage', action.value);

    case "ORGANIZATIONS_DETAILS_UPDATE":
      return state.set('details', action.value);

    default:
      return state;
  }
}
