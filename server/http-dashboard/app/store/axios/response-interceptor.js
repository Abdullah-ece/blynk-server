export default {
  success: (store, response) => response,
  error: (store, error) => {
    const UNAUTHORIZED_HTTP_CODE = 401;
    if (error.response.status === UNAUTHORIZED_HTTP_CODE) {
      /** @todo rewrite to window.location when we start to use html5 history */
      window.location.hash = '/logout';
      return error;
    } else {
      if (error.response && !error.response.message) {
        error.response.message = error &&
          error.response &&
          error.response.data &&
          error.response.data.error &&
          error.response.data.error.message &&
          String(error.response.data.error.message);
      }
      return Promise.reject(error);
    }
  }
};
