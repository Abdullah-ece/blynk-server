import moment from 'moment';

export const Metadata = {
  Fields: {
    TEXT: 'Text',
    NUMBER: 'Number',
    COST: 'Cost',
    TIME: 'Time',
    COORDINATES: 'Coordinates',
    UNIT: 'Measurement',
    SHIFT: 'Shift',
    SWITCH: 'Switch',
    DATE: 'Date',
    CONTACT: 'Contact',
  }
};

export const Unit = {
  Inch: {
    abbreviation: 'in',
    key: 'Inch',
    value: 'Inch',
  },
  Foot: {
    abbreviation: 'ft',
    key: 'Foot',
    value: 'Foot',
  },
  Yard: {
    abbreviation: 'yd',
    key: 'Yard',
    value: 'Yard',
  },
  Mile: {
    abbreviation: 'mi',
    key: 'Mile',
    value: 'Mile',
  },
  Millimeter: {
    abbreviation: 'mm',
    key: 'Millimeter',
    value: 'Millimeter',
  },
  Centimeter: {
    abbreviation: 'cm',
    key: 'Centimeter',
    value: 'Centimeter',
  },
  Meter: {
    abbreviation: 'm',
    key: 'Meter',
    value: 'Meter',
  },
  Kilometer: {
    abbreviation: 'km',
    key: 'Kilometer',
    value: 'Kilometer',
  },
  Ounce: {
    abbreviation: 'oz',
    key: 'Ounce',
    value: 'Ounce',
  },
  Pound: {
    abbreviation: 'lb',
    key: 'Pound',
    value: 'Pound',
  },
  Stone: {
    abbreviation: 'st',
    key: 'Stone',
    value: 'Stone',
  },
  Quarter: {
    abbreviation: 'qrt',
    key: 'Quarter',
    value: 'Quarter',
  },
  Hundredweight: {
    abbreviation: 'cwt',
    key: 'Hundredweight',
    value: 'Hundredweight',
  },
  Ton: {
    abbreviation: 't',
    key: 'Ton',
    value: 'Ton',
  },
  Milligram: {
    abbreviation: 'mg',
    key: 'Milligram',
    value: 'Milligram',
  },
  Gram: {
    abbreviation: 'g',
    key: 'Gram',
    value: 'Gram',
  },
  Kilogram: {
    abbreviation: 'kg',
    key: 'Kilogram',
    value: 'Kilogram',
  },
  Tonne: {
    abbreviation: 't',
    key: 'Tonne',
    value: 'Tonne',
  },
  Pint: {
    abbreviation: 'pt',
    key: 'Pint',
    value: 'Pint',
  },
  Gallon: {
    abbreviation: 'gal',
    key: 'Gallon',
    value: 'Gallon',
  },
  Liter: {
    abbreviation: 'lt',
    key: 'Liter',
    value: 'Liter',
  },
  Celsius: {
    abbreviation: '°C',
    key: 'Celsius',
    value: 'Celsius',
  },
  Fahrenheit: {
    abbreviation: '°F',
    key: 'Fahrenheit',
    value: 'Fahrenheit',
  },
  Kelvin: {
    abbreviation: '°K',
    key: 'Kelvin',
    value: 'Kelvin',
  }
};

export const Currency = {
  USD: {
    abbreviation: '$',
    key: 'USD',
    value: 'USD'
  },
  EUR: {
    abbreviation: '€',
    key: 'EUR',
    value: 'EUR'
  },
  GBP: {
    abbreviation: '£',
    key: 'GBP',
    value: 'GBP'
  },
  CNY: {
    abbreviation: '¥',
    key: 'CNY',
    value: 'CNY'
  },
  RUB: {
    abbreviation: '₽',
    key: 'RUB',
    value: 'RUB'
  }
};

export const transformTimeToTimestamp = (data) => {
  if (data.metaFields) {
    data.metaFields = data.metaFields.map((field) => {
      if (field.type === Metadata.Fields.TIME && field.value) {
        return {
          ...field,
          value: (moment().hours(field.value.split(':')[0]).minutes(field.value.split(':')[1]).valueOf())
        };
      }
      return field;
    });
  }
  return data;
};

export const transformShiftToMinutes = (data) => {
  if (data.metaFields) {
    data.metaFields = data.metaFields.map((field) => {
      if (field.type === Metadata.Fields.SHIFT && field.to) {
        field = {
          ...field,
          from: (moment.duration(field.from).asMinutes())
        };
      }
      if (field.type === Metadata.Fields.SHIFT && field.from) {
        field = {
          ...field,
          to: (moment.duration(field.to).asMinutes())
        };
      }
      return field;
    });
  }
  return data;
};
