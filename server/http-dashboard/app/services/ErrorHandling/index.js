export const displayError = (err, fn) => {
  try {
    let errorMessage = err.error.response.data.error.message;

    fn(errorMessage);
  } catch (e) {
    alert('Error happened');
  }
};
