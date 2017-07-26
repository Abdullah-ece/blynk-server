export function OrganizationsFetch() {
  return {
    type: 'API_ORGANIZATIONS_FETCH',
    payload: {
      request: {
        method: 'get',
        url: `/organization`
      }
    }
  };
}

export function OrganizationsCreate(data) {
  return {
    type: 'API_ORGANIZATIONS_CREATE',
    payload: {
      request: {
        method: 'put',
        url: `/organization`,
        data: data
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
    payload: {
      request: {
        method: 'post',
        url: `/organization/${data.id}`,
        data: data
      }
    }
  };
}

export function OrganizationsUsersFetch(data) {
  if (!data.id)
    throw Error('Organization id is not specified');
  return {
    type: 'API_ORGANIZATIONS_USERS_FETCH',
    payload: {
      request: {
        method: 'get',
        url: `/organization/${data.id}/users`
      }
    }
  };
}

export function OrganizationsDelete(data) {
  if (!data.id)
    throw Error('Organization id is not specified');
  return {
    type: 'API_ORGANIZATIONS_DELETE',
    payload: {
      request: {
        method: 'delete',
        url: `/organization/${data.id}`
      }
    }
  };
}

export function OrganizationsCanInvite(data) {
  return {
    type: 'API_ORGANIZATIONS_CAN_INVITE',
    payload: {
      request: {
        method: 'post',
        url: `/organization/${data.id}/canInviteUser`,
        data: {
          email: data.email
        }
      }
    }
  };
}
