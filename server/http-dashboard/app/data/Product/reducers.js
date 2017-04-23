const initialState = {
  creating: {
    info: {
      invalid: false
    },
    metadata: {
      invalid: false,
      fields: []
    }
  },
  products: []
};

export default function Product(state = initialState, action) {
  switch (action.type) {

    case "API_PRODUCTS_SUCCESS":
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
        info: {
          ...state.info,
          invalid: action.data
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
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: [
              ...state.creating.metadata.fields.map((field) => {
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
