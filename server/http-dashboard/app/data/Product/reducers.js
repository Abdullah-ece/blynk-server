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
  },
  products: []
};

export default function Product(state = initialState, action) {
  switch (action.type) {

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
          ...prepareProductForEdit(action.data)
        }
      };

    case "API_PRODUCTS_FETCH_SUCCESS":
      return {
        ...state,
        products: action.payload.data
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
