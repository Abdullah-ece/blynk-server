export function ProductCreate(data = false) {
  return {
    type: 'API_PRODUCT_CREATE',
    payload: {
      request: {
        method: 'put',
        url: '/product',
        data: data
      }
    }
  };
}
