const initialState = {
  name: '',
  tzName: 'Select timezone', // default timezone if not specified on backend
  primaryColor: 'f00a0a',
  secondaryColor: '979282',
  logoUrl: '',
  users: []
};

export default function Account(state = initialState, action) {
  switch (action.type) {

    case "API_ORGANIZATION_SUCCESS":
      return {
        ...state,
        ...action.payload.data
      };
    case "API_ORGANIZATION_USERS_SUCCESS":
      return {
        ...state,
        users: [...action.payload.data]
      };
    case "API_ORGANIZATION_FAILURE":
      return {
        ...state,
      };

    case "API_ORGANIZATION_SAVE_SUCCESS":
      return {
        ...state,
        ...action.payload.data
      };
    case "API_ORGANIZATION_SAVE_FAILURE":
      return {
        ...state,
      };

    case "ORGANIZATION_BRANDING_UPDATE":
      return {
        ...state,
        primaryColor: action.colors.primaryColor || 0,
        secondaryColor: action.colors.secondaryColor || 0
      };

    case "ORGANIZATION_UPDATE_LOGO":
      return {
        ...state,
        logo: action.logo
      };

    case "ORGANIZATION_UPDATE_NAME":
      return {
        ...state,
        name: action.name
      };

    default:
      return state;
  }
}
