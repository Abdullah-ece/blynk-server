const initialState = {
  roles: []
};

export default function RolesAndPermissions(state = initialState, action) {
  switch (action.type) {
    case "WEB_GET_ROLES_SUCCESS":
      return {
        ...state,
        roles: (Object.values(action.payload.data).map(
          ({
             id,
             name,
             permissionGroup1,
             permissionGroup2
           }) => {
            return {
              id,
              name,
              permissionGroup1,
              permissionGroup2,
              permissionsGroup1Binary: (permissionGroup1 >>> 0).toString(2)
            };
          }))
          // Skip super admin!!! This role has id of 0
          .filter(role => role.id)
      };

    case "WEB_UPDATE_ROLE":
      const data = JSON.parse(action.ws.request.query);
      return {
        ...state,
        roles: [...state.roles].map((role) => {
          return role.id === data.id ? {
            ...role,
            permissionGroup1: data.permissionGroup1,
            permissionsGroup1Binary: (data.permissionGroup1 >>> 0).toString(2)
          } : role;
        })
      };

    default:
      return state;
  }
}
