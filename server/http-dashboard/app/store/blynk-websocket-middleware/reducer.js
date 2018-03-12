import {fromJS} from 'immutable';
import {ACTIONS} from './actions';

const initialState = fromJS({
  messages: [
    // {
    //   id: 0,
    //   request: {
    //     command: COMMANDS.LOGIN,
    //     value: '',
    //   },
    //   response: {
    //     command: COMMANDS.RESPONSE,
    //     responseCode: RESPONSE_CODES.OK,
    //     value: ''
    //   }
    // }
  ],
  trackDeviceId: null,
  trackOnlyByDeviceId: true
});

export default function createReducer(state = initialState, action) {

  switch (action.type) {

    case ACTIONS.BLYNK_WS_TRACK_DEVICE_ID:
      return state.set('trackDeviceId', action.value.deviceId);

    case ACTIONS.BLYNK_WS_REQUEST:

      return state.update('messages', (messages) => messages.push(fromJS({
        id      : action.value.id,
        request : action.value.request,
        response: null
      })));

    case ACTIONS.BLYNK_WS_RESPONSE:

      return state.update('messages', (messages) => {

        const index = messages.findIndex((message) => {
          return Number(message.get('id')) === Number(action.value.id);
        });

        if (index >= 0) {
          return messages.setIn([index, 'response'], action.value.response);
        } else {
          return messages.push(fromJS({
            id      : action.value.id,
            request : null,
            response: action.value.response
          }));
        }

      });

    default:
      return state;

  }

}
