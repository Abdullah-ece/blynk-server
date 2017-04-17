export const transformJsonToFormUrlEncoded = (data) => {
  const str = [];
  for (let p in data)
    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(data[p]));
  return str.join("&");
};

export function formHasError(fieldsErrors) {
  const errors = [];
  Object.keys(fieldsErrors).forEach((key) => {
    if (fieldsErrors[key])
      errors.push(fieldsErrors[key]);
  });

  return !!errors.length;
}

export function getFormFirstError(fieldsErrors) {
  const errors = [];
  Object.keys(fieldsErrors).forEach((key) => {
    if (fieldsErrors[key])
      errors.push(fieldsErrors[key]);
  });

  return errors.length ? errors[0] : null;
}
