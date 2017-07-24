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
