import { API_COMMANDS } from "store/blynk-websocket-middleware/commands";

export function GetRuleGroup() {
  return {
    type: 'WEB_GET_RULE_GROUP',
    ws: {
      request: {
        command: API_COMMANDS.WEB_GET_RULE_GROUP,
      }
    }
  };
}

export function UpdateRuleGroup(data) {
  return {
    type: 'WEB_EDIT_RULE_GROUP',
    ws: {
      request: {
        command: API_COMMANDS.WEB_EDIT_RULE_GROUP,
        query: [JSON.stringify(data)]
      }
    }
  };
}
