import {fromJS} from 'immutable';

const initialState = fromJS({
  list: null,
  details: {
    activeTab: 'info', //hardcoded info tab key
    users: null,
    userDeleteLoading: false,
    userInviteLoading: false,
    organizationDeleteLoading: false,
  },
  manage: {
    activeTab: 'info', //hardcoded info tab key
    loading: false,
    info: {
      form: ''
    },
    products: {
      form: ''
    },
    admins: {
      form: ''
    }
  },
  adminTableListOptions: {
    sortInfo: {
      order: 'ascend',
      columnKey: 'name'
    },
    selectedRows: [],
    loading: false,
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

    case "ORGANIZATIONS_ADMIN_TABLE_LIST_UPDATE_SELECTED_ROWS":
      return state.setIn(['adminTableListOptions', 'selectedRows'], fromJS(action.value));

    case "ORGANIZATIONS_ADMIN_TABLE_LIST_UPDATE_SORT_INFO":
      return state.setIn(['adminTableListOptions', 'sortInfo'], fromJS(action.value));

    case "API_ORGANIZATIONS_USERS_FETCH_SUCCESS":
      return state.setIn(['details', 'users'], fromJS(action.payload.data));

    default:
      return state;
  }
}
