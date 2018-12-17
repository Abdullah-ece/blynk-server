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
      };

    case "WEB_UPDATE_ROLE":
      return {
        ...state,
        roles: [...state.roles.roles].map((role) => {
          return role.id === action.payload.id ? {
            ...role,
            permissionGroup1: action.payload,
            permissionsGroup1Binary: (action.payload.permissionGroup1 >>> 0).toString(2)
          } : role;
        })
      };

    default:
      return state;
  }
}
