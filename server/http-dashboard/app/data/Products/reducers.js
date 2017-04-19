const initialState = {
  creating: {
    metadata: {
      fields: []
    }
  }
};

export default function Account(state = initialState, action) {
  switch (action.type) {
    case "PRODUCT_METADATA_FIELD_ADD":
      return {
        ...state,
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: state.creating.metadata.fields.concat({
              ...action.data,
              id: state.creating.metadata.fields.reduce((acc, value) => (
                acc < value.id ? value.id : acc
              ), state.creating.metadata.fields.length ? state.creating.metadata.fields[0].id : 0) + 1
            })
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

    default:
      return state;
  }
}

