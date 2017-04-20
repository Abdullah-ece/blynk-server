export const isFormValid = (form) => {
  return Object.keys(form.getFieldsError()).every((field) => !form.getFieldsError()[field]);
};

export const Messages = {
  username: 'Username must contain only letters and numbers',
  fullname: 'Full name is incorrect',
  password: 'Password must contain only letters and numbers',
  email: 'Email is not correct',
  required: 'Field is required',
  minLength: (n) => `Minimal length should be ${n} symbols`,
  number: 'Field should contain number'
};

export const Rules = {
  username: (value) => !/^[a-z0-9]+$/.test(value) ? Messages.username : undefined,
  /** @todo find best correct regex for fullname */
  fullname: (value) => !/^[a-zA-Z ]+$/.test(value) ? Messages.fullname : undefined,
  password: (value) => !/^[a-z0-9]+$/.test(value) ? Messages.password : undefined,
  email: (value) => !/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(value) ? Messages.email : undefined,
  minLength: (n) => (value) => !value || value.length < n ? Messages.minLength(n) : undefined,
  required: (value) => !value ? Messages.required : undefined,
  number: (value) => isNaN(Number(value)) ? Messages.number : undefined
};

const Validation = {
  isFormValid: isFormValid,
  Rules: Rules,
  Messages: Messages
};

export default Validation;
