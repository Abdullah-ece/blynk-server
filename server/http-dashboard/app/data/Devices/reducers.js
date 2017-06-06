import {fromJS, Map} from 'immutable';

const initialState = Map({
  devices: []
});

export default function Devices(state = initialState, action) {

  switch (action.type) {
    case "API_DEVICES_FETCH_SUCCESS":
      return state.set('devices',
        fromJS(action.payload.data).map((device) => device.set('metaFields', fromJS([
          {
            type: "Range",
            name: "Range for Farm of Smith",
            role: "ADMIN",
            from: 60,
            to: 120
          },
          {
            type: "Measurement",
            name: "Farm of Smith",
            role: "ADMIN",
            units: "Celsius",
            value: 36
          },
            {
              type: "Text",
              name: "Device Name",
              role: "ADMIN",
              value: "My Device 0"
            },
            {
              type: "Text",
              name: "Device Owner",
              role: "ADMIN",
              value: "ihor.bra@gmail.com"
            },
            {
              type: "Text",
              name: "Location Name",
              role: "ADMIN",
              value: "Trenton New York Farm"
            },
          {
            type: "Number",
            name: "Cost of Pump 1",
            role: "ADMIN",
            value: 10.23
            }
          ]))
        )
      );

    case "API_DEVICES_UPDATE_SUCCESS":
      return state.set('devices',
        fromJS(action.payload.data).map((device) => device.set('metaFields', fromJS([
            {
              type: "Text",
              name: "Device Name",
              role: "ADMIN",
              value: "My Device 0"
            },
            {
              type: "Text",
              name: "Device Owner",
              role: "ADMIN",
              value: "ihor.bra@gmail.com"
            },
            {
              type: "Text",
              name: "Location Name",
              role: "ADMIN",
              value: "Trenton New York Farm"
            }
          ]))
        )
      );

    default:
      return state;
  }

}
