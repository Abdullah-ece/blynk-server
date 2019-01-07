const initialState = {
  ruleEngine: ""
};

export default function RulesEngine(state = initialState, action) {
  switch (action.type) {
    case "WEB_GET_RULE_GROUP_SUCCESS":
      return {
        ...state,
        ruleEngine: action.payload.data,
      };
    case "WEB_EDIT_RULE_GROUP":
      return {
        ...state,
        ruleEngine: action.data,
      };
    default:
      return state;
  }
}
