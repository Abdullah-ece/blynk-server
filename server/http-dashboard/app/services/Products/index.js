import _ from 'lodash';
import moment from 'moment';
import 'moment-duration-format';

export const Metadata = {
  Fields: {
    TEXT: 'Text',
    NUMBER: 'Number',
    COST: 'Cost',
    TIME: 'Time',
    COORDINATES: 'Coordinates',
    UNIT: 'Measurement',
    RANGE: 'Range',
    SWITCH: 'Switch',
    DATE: 'Date',
    CONTACT: 'Contact'
  }
};

export const Unit = {
  Inch: {
    abbreviation: 'in',
    key: 'Inch',
    value: 'Inch'
  },
  Foot: {
    abbreviation: 'ft',
    key: 'Foot',
    value: 'Foot'
  },
  Yard: {
    abbreviation: 'yd',
    key: 'Yard',
    value: 'Yard'
  },
  Mile: {
    abbreviation: 'mi',
    key: 'Mile',
    value: 'Mile'
  },
  Millimeter: {
    abbreviation: 'mm',
    key: 'Millimeter',
    value: 'Millimeter'
  },
  Centimeter: {
    abbreviation: 'cm',
    key: 'Centimeter',
    value: 'Centimeter'
  },
  Meter: {
    abbreviation: 'm',
    key: 'Meter',
    value: 'Meter'
  },
  Kilometer: {
    abbreviation: 'km',
    key: 'Kilometer',
    value: 'Kilometer'
  },
  Ounce: {
    abbreviation: 'oz',
    key: 'Ounce',
    value: 'Ounce'
  },
  Pound: {
    abbreviation: 'lb',
    key: 'Pound',
    value: 'Pound'
  },
  Stone: {
    abbreviation: 'st',
    key: 'Stone',
    value: 'Stone'
  },
  Quarter: {
    abbreviation: 'qrt',
    key: 'Quarter',
    value: 'Quarter'
  },
  Hundredweight: {
    abbreviation: 'cwt',
    key: 'Hundredweight',
    value: 'Hundredweight'
  },
  Ton: {
    abbreviation: 't',
    key: 'Ton',
    value: 'Ton'
  },
  Milligram: {
    abbreviation: 'mg',
    key: 'Milligram',
    value: 'Milligram'
  },
  Gram: {
    abbreviation: 'g',
    key: 'Gram',
    value: 'Gram'
  },
  Kilogram: {
    abbreviation: 'kg',
    key: 'Kilogram',
    value: 'Kilogram'
  },
  Pint: {
    abbreviation: 'pt',
    key: 'Pint',
    value: 'Pint'
  },
  Gallon: {
    abbreviation: 'gal',
    key: 'Gallon',
    value: 'Gallon'
  },
  Liter: {
    abbreviation: 'lt',
    key: 'Liter',
    value: 'Liter'
  },
  Celsius: {
    abbreviation: '°C',
    key: 'Celsius',
    value: 'Celsius'
  },
  Fahrenheit: {
    abbreviation: '°F',
    key: 'Fahrenheit',
    value: 'Fahrenheit'
  },
  Kelvin: {
    abbreviation: '°K',
    key: 'Kelvin',
    value: 'Kelvin'
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
      if (field.type === Metadata.Fields.TIME && field.time) {
        return {
          ...field,
          time: Math.round((moment().hours(field.time.split(':')[0]).minutes(field.time.split(':')[1]).valueOf()) / 1000)
        };
      }
      return field;
    });
  }
  return data;
};

export const transformTimestampToTime = (data) => {

  if (Array.isArray(data)) {

    data.forEach((product) => {
      if (product.metaFields) {
        product.metaFields = product.metaFields.map((field) => {
          if (field.type === Metadata.Fields.TIME && field.time) {
            const time = moment(field.time * 1000);
            return {
              ...field,
              time: `${time.format('HH:mm')}`
            };
          }
          return field;
        });
      }
    });

  }
  return data;
};

export const transformMinutesToShift = (data) => {

  if (Array.isArray(data)) {

    data.forEach((product) => {
      if (product.metaFields) {
        product.metaFields = product.metaFields.map((field) => {
          if (field.type === Metadata.Fields.RANGE && !isNaN(Number(field.from))) {
            field = {
              ...field,
              from: !field.from ? '00:00' : (moment.duration(field.from, 'minutes').format('HH:mm'))
            };
          }
          if (field.type === Metadata.Fields.RANGE && !isNaN(Number(field.to))) {
            field = {
              ...field,
              to: !field.to ? '00:00' : (moment.duration(field.to, 'minutes').format('HH:mm'))
            };
          }
          return field;
        });
      }
    });

  }
  return data;
};

export const transformRangeToMinutes = (data) => {
  if (data.metaFields) {
    data.metaFields = data.metaFields.map((field) => {
      if (field.type === Metadata.Fields.RANGE && field.from) {
        field = {
          ...field,
          from: (moment.duration(field.from).asMinutes())
        };
      }
      if (field.type === Metadata.Fields.RANGE && field.to) {
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

export const prepareProductForEdit = (data) => {

  const edit = {
    info: {
      values: {}
    },
    metadata: {
      fields: {}
    }
  };

  edit.info.values = _.pickBy(data, (value, key) => {
    return key !== 'metaFields';
  });

  let id = 1;
  edit.metadata.fields = (data.metaFields && data.metaFields.map((field) => {
      return {
        id: ++id,
        type: field.type,
        values: _.pickBy(field, (value, key) => key !== 'type')
      };
    })) || [];

  return edit;

};

export const prepareProductForSave = (data) => {

  const product = {
    ...data.info.values,
    metaFields: []
  };

  if (Array.isArray(data.metadata.fields)) {
    data.metadata.fields.forEach((value) => {
      product.metaFields.push({
        name: value.name,
        type: value.type,
        ...value.values
      });
    });
  }

  return product;
};
