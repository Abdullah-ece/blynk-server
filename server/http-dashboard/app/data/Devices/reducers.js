const initialState = {
  devices: []
};

export default function Devices(state = initialState, action) {

  switch (action.type) {
    case "API_DEVICES_FETCH_SUCCESS":
      return {
        ...state,
        devices: action.payload.data
      };

    default:
      return state;
  }

}
