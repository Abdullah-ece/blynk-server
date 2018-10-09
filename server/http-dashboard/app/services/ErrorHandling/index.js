export const displayError = (err, fn) => {
  try {
    let errorMessage = err.error.response.data.error.message;

    if(typeof fn === 'function')
      fn(errorMessage);

    return errorMessage;

  } catch (e) {
    alert('Error happened');
  }
};
