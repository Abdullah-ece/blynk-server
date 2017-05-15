export const isLoggedIn = (store) => {
  if (!store.getState) {
    throw new Error('isLoggedIn wrong parameter store');
  }

  const state = store.getState();

  return state && state.Login && state.Login.isLoggedIn;
};

export const RouteAuthorizedOnly = (store) => {
  return (nextState, replaceWith, callback) => {
    if (!isLoggedIn(store)) {
      replaceWith('/');
    }
    callback();
  };
};

export const RouteGuestOnly = (store) => {
  return (nextState, replaceWith, callback) => {
    if (isLoggedIn(store)) {
      replaceWith('/products');
    }
    callback();
  };
};


