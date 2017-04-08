export function OrganizationFetch() {
  return {
    type: 'API_ORGANIZATION',
    payload: {
      request: {
        method: 'get',
        url: '/organization'
      }
    }
  };
}

export function OrganizationSave(data) {
  return {
    type: 'API_ORGANIZATION_SAVE',
    payload: {
      request: {
        method: 'post',
        url: '/organization',
        data: data
      }
    }
  };
}

export function OrganizationUpdateName(name) {
  return {
    type: 'ORGANIZATION_UPDATE_NAME',
    name: name
  };
}

export function OrganizationUpdateTimezone(tzName) {
  return {
    type: 'ORGANIZATION_UPDATE_TIMEZONE',
    tzName: tzName
  };
}
