const initialState = {
  activeTab: null
};


export default function UserProfile(state = initialState, action) {
  switch (action.type) {
    case "TAB_CHANGE":
      return {
        ...state,
        activeTab: action.data
      };

    default:
      return state;
  }
}

