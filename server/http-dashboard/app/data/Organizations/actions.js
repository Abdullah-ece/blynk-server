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

export function OrganizationsManageSetActiveTab(tab) {
  return {
    type: 'ORGANIZATIONS_MANAGE_SET_ACTIVE_TAB',
    value: tab
  };
}
