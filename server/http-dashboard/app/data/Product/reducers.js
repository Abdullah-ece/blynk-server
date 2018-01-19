import {prepareProductForEdit} from 'services/Products';

const initialState = {
  creating: {
    info: {
      invalid: true,
      values: {}
    },
    metadata: {
      invalid: true,
      fields: []
    }
  },
  edit: {
    entity: {},
    info: {
      invalid: true,
      values: {}
    },
    metadata: {
      invalid: true,
      fields: []
    },
    dataStreams: {
      invalid: true,
      fields: []
    },
    events: {
      invalid: true,
      fields: []
    },
    dashboard: {
      isLoading: false,
      devicesList: [],
      selectedDeviceId: undefined,
    }
  },
  products: null
};

export default function Product(state = initialState, action) {
  switch (action.type) {

    case "PRODUCT_DASHBOARD_DEVICE_ID_FOR_PREVIEW_CHANGE":
      return {
        ...state,
        edit: {
          ...state.edit,
          dashboard: {
            ...state.edit.dashboard,
            selectedDeviceId: action.data
          }
        }
      };

    case "API_DEVICES_LIST_FOR_PRODUCT_DASHBOARD_PREVIEW_FETCH":
      return {
        ...state,
        edit: {
          ...state.edit,
          dashboard: {
            ...state.edit.dashboard,
            isLoading: true,
          }
        }
      };

    case "API_DEVICES_LIST_FOR_PRODUCT_DASHBOARD_PREVIEW_FETCH_SUCCESS":
      return {
        ...state,
        edit: {
          ...state.edit,
          dashboard: {
            ...state.edit.dashboard,
            devicesList: action.payload.data,
            isLoading: false,
          }
        }
      };

    case "API_DEVICES_LIST_FOR_PRODUCT_DASHBOARD_PREVIEW_FETCH_FAILURE":
      return {
        ...state,
        edit: {
          ...state.edit,
          dashboard: {
            ...state.edit.dashboard,
            isLoading: false,
          }
        }
      };


    case "PRODUCT_EDIT_METADATA_FIELDS_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          metadata: {
            ...state.edit.metadata,
            fields: action.data
          }
        }
      };

    case "PRODUCT_EDIT_EVENTS_FIELDS_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          events: {
            ...state.edit.events,
            fields: action.data
          }
        }
      };

    case "PRODUCT_EDIT_DATA_STREAMS_FIELDS_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          dataStreams: {
            ...state.edit.dataStreams,
            fields: action.data
          }
        }
      };

    case "PRODUCT_EDIT_METADATA_FIELD_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          metadata: {
            ...state.edit.metadata,
            fields: state.edit.metadata.fields.map((field) => field.id !== action.data.id ? field : action.data)
          }
        }
      };

    case "PRODUCT_EDIT_DATA_STREAMS_FIELD_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          dataStreams: {
            ...state.edit.dataStreams,
            fields: state.edit.dataStreams.fields.map((field) => field.id !== action.data.id ? field : action.data)
          }
        }
      };

    case "API_PRODUCT_DELETE_SUCCESS":
      return {
        ...state
      };

    case "PRODUCT_SET_EDIT":
      return {
        ...state,
        edit: {
          ...state.edit,
          entity: action.data, // to check with original entity meta/event/datastreams ids if something was delete/create
          ...prepareProductForEdit(action.data)
        }
      };

    case "API_PRODUCTS_FETCH_SUCCESS":
      return {
        ...state,
        products: action.payload.data
      };

    case "API_PRODUCT_FETCH_SUCCESS":

      let products = (state.products || []).filter((product) => product.id !== action.payload.data.id);

      products = products.concat(action.payload.data);

      return {
        ...state,
        products: products
      };

    case "PRODUCT_METADATA_FIELD_ADD":
      return {
        ...state,
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: state.creating.metadata.fields.concat({
              invalid: false,
              ...action.data,
              id: state.creating.metadata.fields.reduce((acc, value) => (
                acc < value.id ? value.id : acc
              ), state.creating.metadata.fields.length ? state.creating.metadata.fields[0].id : 0) + 1,
            })
          }
        }
      };

    case "PRODUCT_INFO_UPDATE_INVALID_FLAG":
      return {
        ...state,
        edit: {
          ...state.edit,
          info: {
            ...state.edit.info,
            invalid: action.data
          }
        }
      };

    case "PRODUCT_METADATA_UPDATE_INVALID_FLAG":
      return {
        ...state,
        metadata: {
          ...state.metadata,
          invalid: action.data
        }
      };

    case "PRODUCT_EDIT_CLEAR_FIELDS":
      return {
        ...state,
        edit: {
          info: {
            invalid: true,
            values: {}
          },
          metadata: {
            invalid: true,
            fields: []
          },
          dataStreams: {
            invalid: true,
            fields: []
          }
        }
      };

    case "PRODUCT_EDIT_INFO_VALUES_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          info: {
            ...state.edit.info,
            values: {
              ...state.edit.info.values,
              ...action.data
            }
          }
        }
      };

    case "PRODUCT_METADATA_FIELD_DELETE":

      return {
        ...state,
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: [
              ...state.creating.metadata.fields.filter((i) => i.id !== action.data.id)
            ]
          }
        }
      };

    case "PRODUCT_METADATA_FIELDS_ORDER_UPDATE":

      return {
        ...state,
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: [
              ...action.data
            ]
          }
        }
      };

    case "PRODUCT_METADATA_FIELD_VALUES_UPDATE":

      return {
        ...state,
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: [
              ...state.creating.metadata.fields.map((field) => {
                if (field.id === action.data.id) {
                  return Object.assign({}, field, {
                    values: action.data.values
                  });
                }
                return field;
              })
            ]
          }
        }
      };

    case "PRODUCT_METADATA_FIELD_INVALID_FLAG_UPDATE":

      return {
        ...state,
        edit: {
          ...state.edit,
          metadata: {
            ...state.edit.metadata,
            fields: [
              ...state.edit.metadata.fields.map((field) => {
                if (field.id === action.data.id) {
                  return Object.assign({}, field, {
                    invalid: action.data.invalid
                  });
                }
                return field;
              })
            ]
          }
        }
      };

    default:
      return state;
  }
}
