export function ProductsFetch() {
  return {
    type: 'API_PRODUCTS',
    payload: {
      request: {
        method: 'get',
        url: '/product'
      }
    }
  };
}
