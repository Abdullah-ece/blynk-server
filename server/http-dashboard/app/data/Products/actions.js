export function ProductMetadataFieldAdd(data = {}) {
  return {
    type: 'PRODUCT_METADATA_FIELD_ADD',
    data: data
  };
}

export function ProductMetadataFieldDelete(data = {}) {
  return {
    type: 'PRODUCT_METADATA_FIELD_DELETE',
    data: data
  };
}

export function ProductMetadataFieldValuesUpdate(data = {}) {
  return {
    type: 'PRODUCT_METADATA_FIELD_VALUES_UPDATE',
    data: data
  };
}

export function ProductMetadataFieldsOrderUpdate(data = {}) {
  return {
    type: 'PRODUCT_METADATA_FIELDS_ORDER_UPDATE',
    data: data
  };
}
