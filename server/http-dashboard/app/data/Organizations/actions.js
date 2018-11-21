import {API_COMMANDS} from "store/blynk-websocket-middleware/commands";

export function OrganizationsFetch() {
  return {
    type: 'API_ORGANIZATIONS_FETCH',
    ws: {
      request: {
        command: API_COMMANDS.GET_ORGS,
      }
    }
  };
}

export function OrganizationsCreate(data) {
  return {
    type: 'API_ORGANIZATIONS_CREATE',
    ws: {
      request: {
        command: API_COMMANDS.CREATE_ORG,
        query: [
          JSON.stringify(data)
        ],
      }
    }
  };
}

export function OrganizationsHierarchyFetch() {
  return {
    type: 'API_ORGANIZATIONS_HIERARCHY_FETCH',
    ws: {
      request: {
        command: API_COMMANDS.GET_ORG_HIERARCHY,
      }
    }
  };
}

export function OrganizationsDetailsUpdate(details) {
  return {
    type: 'ORGANIZATIONS_DETAILS_UPDATE',
    value: details
  };
}

export function OrganizationsAdminsInviteLoadingToggle(state) {
  return {
    type: 'ORGANIZATIONS_ADMINS_INVITE_LOADING_TOGGLE',
    value: state
  };
}

export function OrganizationsAdminsDeleteLoadingToggle(state) {
  return {
    type: 'ORGANIZATIONS_ADMINS_DELETE_LOADING_TOGGLE',
    value: state
  };
}

export function OrganizationsManageSetActiveTab(tab) {
  return {
    type: 'ORGANIZATIONS_MANAGE_SET_ACTIVE_TAB',
    value: tab
  };
}

export function OrganizationsManageUpdate(manage) {
  return {
    type: 'ORGANIZATIONS_MANAGE_UPDATE',
    value: manage
  };
}

export function OrganizationsAdminTableListUpdateSelectedRows(value) {
  return {
    type: 'ORGANIZATIONS_ADMIN_TABLE_LIST_UPDATE_SELECTED_ROWS',
    value: value
  };
}

export function OrganizationsAdminTableListUpdateSortInfo(value) {
  return {
    type: 'ORGANIZATIONS_ADMIN_TABLE_LIST_UPDATE_SORT_INFO',
    value: value
  };
}

export function OrganizationsUpdate(data = {}) {
  if (!data.id)
    throw Error('Organization id is not specified');
  return {
    type: 'API_ORGANIZATIONS_UPDATE',
    ws: {
      request: {
        command: API_COMMANDS.UPDATE_ORG,
        query: [
          JSON.stringify(data),
        ]
      }
    }
  };
}

export function OrganizationsUsersFetch(data) {
  if (!data.id)
    throw Error('Organization id is not specified');
  return {
    type: 'API_ORGANIZATIONS_USERS_FETCH',
    ws: {
      request: {
        command: API_COMMANDS.GET_ORG_USERS,
        query: [
          data.id,
        ],
      }
    }
  };
}

export function OrganizationsDelete(data) {
  if (!data.id)
    throw Error('Organization id is not specified');
  return {
    type: 'API_ORGANIZATIONS_DELETE',
    ws: {
      request: {
        command: API_COMMANDS.DELETE_ORG,
        query: [
          data.id
        ],
      }
    }
  };
}

export function OrganizationsCanInvite(data) {
  return {
    type: 'API_ORGANIZATIONS_CAN_INVITE',
    ws: {
      request: {
        command: API_COMMANDS.CAN_INVITE_USER,
        query: [
          data.id,
          data.email
        ],
      }
    }
  };
}
