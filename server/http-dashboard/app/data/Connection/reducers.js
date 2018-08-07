export const CONNECTION_STATES_VALUES = {
  NOT_CONNECTED: 0,
  CONNECTING: 1,
  SUCCESS: 2,
  FAILED: 3,
  INTERRUPTED: 4,
};

export const CONNECTION_STATES = {
  0: CONNECTION_STATES_VALUES.NOT_CONNECTED,
  1: CONNECTION_STATES_VALUES.CONNECTING,
  2: CONNECTION_STATES_VALUES.SUCCESS,
  3: CONNECTION_STATES_VALUES.FAILED,
  4: CONNECTION_STATES_VALUES.INTERRUPTED,
};

const initialState = {
  connection: CONNECTION_STATES_VALUES.NOT_CONNECTED,
};

export default function Connection(state = initialState, action) {

  if(action.type === 'CONNECTION_CONNECT')
    return {
      ...state,
      connection: CONNECTION_STATES_VALUES.CONNECTING
    };

  if(action.type === 'CONNECTION_CONNECT_SUCCESS')
    return {
      ...state,
      connection: CONNECTION_STATES_VALUES.SUCCESS
    };

  if(action.type === 'CONNECTION_CONNECT_FAILED')
    return {
      ...state,
      connection: CONNECTION_STATES_VALUES.FAILED
    };

  if(action.type === 'CONNECTION_CONNECTION_INTERRUPTED')
    return {
      ...state,
      connection: CONNECTION_STATES_VALUES.INTERRUPTED
    };


  return state;

}
