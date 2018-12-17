const initialState = {
  roles: []
};

export default function RolesAndPermissions(state = initialState, action) {
  switch (action.type) {
    case "WEB_GET_ROLES_SUCCESS":
      return {
        ...state,
        ...action.payload.data
      };

    default:
      return state;
  }
}
