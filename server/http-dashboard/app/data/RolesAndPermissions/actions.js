import { API_COMMANDS } from "store/blynk-websocket-middleware/commands";

export function GetPermissions() {
  return {
    type: 'WEB_GET_ROLES',
    ws: {
      request: {
        command: API_COMMANDS.WEB_GET_ROLES,
      }
    }
  };
}

export function GetPermissionsForRole(data) {
  return {
    type: 'WEB_GET_ROLE',
    ws: {
      request: {
        command: API_COMMANDS.WEB_GET_ROLE,
        query: [data.roleId]
      }
    }
  };
}
