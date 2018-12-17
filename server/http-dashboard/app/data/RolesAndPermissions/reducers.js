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

    default:
      return state;
  }
}
