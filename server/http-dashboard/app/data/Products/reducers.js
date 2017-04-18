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
            fields: state.creating.metadata.fields.concat(action.data)
          }
        }
      };
    default:
      return state;
  }
}
