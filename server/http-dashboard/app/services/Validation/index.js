export const isFormValid = (form) => {
  return Object.keys(form.getFieldsError()).every((field) => !form.getFieldsError()[field]);
};

export const Messages = {
  username: 'Username must contain only letters and numbers',
  fullname: 'Full name is incorrect',
  password: 'Password must contain only letters and numbers',
  email: 'Email is not correct',
  required: 'Field is required',
  imageRequired: 'Image is required',
  minLength: (n) => `Minimal length should be ${n} symbols`,
  number: 'Only numbers allowed',
  numberLong: 'Number max length is 15 symbols',
  latitude: 'Latitude is not correct',
  longitude: 'Longitude is not correct',
  metafieldName: 'Field is not correct',
  eventsEventCode: 'Event code is not correct'
};

const MAX_NUMBER = 999999999999999;
const MIN_NUMBER = -MAX_NUMBER;

export const Rules = {
  username: (value) => !/^[a-z0-9]+$/.test(value) ? Messages.username : undefined,
  /** @todo find best correct regex for fullname */
  fullname: (value) => !/^[a-zA-Z. -]+$/.test(value) ? Messages.fullname : undefined,
  password: (value) => !/^[a-z0-9]+$/.test(value) ? Messages.password : undefined,
  email: (value) => {
    if (!value) return undefined;
    return !/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(value) ? Messages.email : undefined;
  },
  minLength: (n) => (value) => !value || value.length < n ? Messages.minLength(n) : undefined,
  required: (value) => value === undefined || value === null || (value.trim && value.trim() === ""    ) ? Messages.required : undefined,
  imageRequired: (value) => !value ? Messages.imageRequired : undefined,
  number: (value) => value && isNaN(Number(value)) ? Messages.number : Number(value) > MAX_NUMBER || Number(value) < MIN_NUMBER ? Messages.numberLong : undefined,
  // latitude: (value) => value && !/^([-+]?\d{1,2}[.]\d+)$/.test(value) ? Messages.latitude : undefined,
  // longitude: (value) => value && !/^([-+]?\d{1,3}[.]\d+)$/.test(value) ? Messages.longitude : undefined,
  latitude: (value) => value && isNaN(Number(value)) ? Messages.latitude : undefined,
  longitude: (value) => value && isNaN(Number(value)) ? Messages.longitude : undefined,
  metafieldName: (value) => {
    if (value && !value.trim()) return Messages.metafieldName;
  },
  eventsEventCode: (value) => {
    if (!/^[a-zA-Z0-9_]{1,}$/.test(value)) return Messages.eventsEventCode;
  }
};

const Validation = {
  isFormValid: isFormValid,
  Rules: Rules,
  Messages: Messages
};

export default Validation;
