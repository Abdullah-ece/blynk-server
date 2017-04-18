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
