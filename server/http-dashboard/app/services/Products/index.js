import _ from 'lodash';
import moment from 'moment';
import 'moment-duration-format';

export const TABS = {
  INFO: 'info',
  METADATA: 'metadata',
  DATA_STREAMS: 'datastreams',
  EVENTS: 'events',
  DASHBOARD: 'dashboard'
};

export const FORMS = {
  DASHBOARD: 'products-manage-dashboard-form'
};

export const convertUserFriendlyEventCode = (userFriendlyCode) => {
  if (!userFriendlyCode) return null;

  return String(userFriendlyCode).toLowerCase().replace(/ /g, '_');
};

export const EVENT_TYPES = {
  ONLINE: 'ONLINE',
  OFFLINE: 'OFFLINE',
  INFO: 'INFORMATION',
  WARNING: 'WARNING',
  CRITICAL: 'CRITICAL'
};

export const getEventDefaultName = (type) => {
  if (EVENT_TYPES.ONLINE === type) {
    return 'Device Online';
  }
  if (EVENT_TYPES.OFFLINE === type) {
    return 'Device Offline';
  }
  if (EVENT_TYPES.CRITICAL === type) {
    return 'Critical Event';
  }
  if (EVENT_TYPES.WARNING === type) {
    return 'Warning Event';
  }
  if (EVENT_TYPES.INFO === type) {
    return 'Information Event';
  }
};

export const DEVICE_FORCE_UPDATE = {
  UPDATE_DEVICES: 'update_devices',
  SAVE_WITHOUT_UPDATE: 'save_without_update',
  CLONE_PRODUCT: 'clone_product'
};

export const Metadata = {
  Fields: {
    ADDRESS: 'Address',
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

export const hardcodedRequiredMetadataFieldsNames = {
  DeviceName: 'Device Name',
  DeviceOwner: 'Device Owner',
  LocationName: 'Location Name',
  Manufacturer: 'Manufacturer',
  ModelName: 'Model Name',
  TimezoneOfTheDevice: 'Timezone of the device'
};

export const filterMetadataFields = (fields, filterHardcoded = true) => {
  return fields.filter((field) => filterHardcoded ? field.values.hardcoded : !field.values.hardcoded);
};

export const filterHardcodedMetadataFields = (fields) => {
  return filterMetadataFields(fields, true);
};

export const filterDynamicMetadataFields = (fields) => {
  return filterMetadataFields(fields, false);
};

export const getHardcodedRequiredMetadataFields = ({timezoneDefaultValue, manufacturerDefaultValue}) => {
  return [
    {
      id: 1,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.DeviceName,
      role: 'ADMIN',
      hardcoded: true
    },
    {
      id: 2,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.DeviceOwner,
      role: 'ADMIN',
      hardcoded: true
    },
    {
      id: 3,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.LocationName,
      role: 'ADMIN',
      hardcoded: true
    },
    {
      id: 4,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.Manufacturer,
      value: manufacturerDefaultValue,
      role: 'STAFF',
      hardcoded: true
    },
    {
      id: 5,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.ModelName,
      role: 'ADMIN',
      hardcoded: true
    },
    {
      id: 6,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.TimezoneOfTheDevice,
      value: timezoneDefaultValue || null,
      role: 'USER',
      hardcoded: true
    }
  ];
};

export const exampleMetadataField = {
  type: Metadata.Fields.TEXT,
  name: 'Example: Serial Number',
  role: 'ADMIN'
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
  },
  Percentage: {
    abbreviation: '%',
    key: 'Percentage',
    value: 'Percentage'
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


export const prepareProductForEdit = (data) => {

  const edit = {
    info: {
      values: {}
    },
    metadata: {
      fields: []
    },
    dataStreams: {
      fields: []
    },
    events: {
      fields: []
    }
  };

  edit.info.values = _.pickBy(data, (value, key) => {
    return key !== 'metaFields';
  });

  if (data.metaFields) {
    edit.metadata.fields = (data.metaFields && data.metaFields.map((field) => {
        let values = _.pickBy(field, (value, key) => key !== 'type');

        const hardcodedFields = [
          hardcodedRequiredMetadataFieldsNames.DeviceName,
          hardcodedRequiredMetadataFieldsNames.DeviceOwner,
          hardcodedRequiredMetadataFieldsNames.LocationName,
          hardcodedRequiredMetadataFieldsNames.Manufacturer,
          hardcodedRequiredMetadataFieldsNames.ModelName,
          hardcodedRequiredMetadataFieldsNames.TimezoneOfTheDevice,
        ];

        if (field.type === Metadata.Fields.CONTACT) {
          values = prepareContactValuesForEdit(values);
        }

        if (hardcodedFields.indexOf(values.name) !== -1) {
          values = {
            ...values,
            hardcoded: true
          };
        }

        values = {
          ...values,
          isSavedBefore: true
        };

        delete values.id;

        return {
          id: field.id,
          type: field.type,
          values: values,
        };
      })) || [];
  }

  if (data.dataStreams) {
    edit.dataStreams.fields = (data.dataStreams && data.dataStreams.map((field) => {
        return {
          id: field.id,
          values: field
        };
      })) || [];
  }

  if (data.events) {
    edit.events.fields = (data.events && data.events.map((field) => {

        if (field.type === EVENT_TYPES.OFFLINE) {
          let hours, minutes;
          if (!isNaN(Number(field.ignorePeriod))) {
            hours = moment.duration(field.ignorePeriod, 'seconds').hours();
            minutes = moment.duration(field.ignorePeriod, 'seconds').minutes();
          }
          field = {
            ...field,
            ignorePeriod: moment().hours(hours || 0).minutes(minutes || 0).format()
          };
        }

        const prepareNotifications = (event, type) => {
          if (event && Array.isArray(event[type])) {
            return event[type].map((notification) => {
              const metadata = _.find(edit.metadata.fields, {values: {name: notification.value}});
              return metadata.id;
            });
          }
          return [];
        };

        let pushNotifications = [];
        if (Array.isArray(field.pushNotifications)) {
          pushNotifications = prepareNotifications(field, 'pushNotifications');
        }

        let emailNotifications = [];
        if (Array.isArray(field.emailNotifications)) {
          emailNotifications = prepareNotifications(field, 'emailNotifications');
        }

        return {
          id: field.id,
          type: field.type,
          values: {
            id: field.id,
            ...field,
            emailNotifications: emailNotifications,
            pushNotifications: pushNotifications
          }
        };
      })) || [];
  }

  return edit;

};

export const prepareProductForClone = (data) => {

  if (data && data.id) {
    delete data.id;
  }

  return data;
};

export const prepareProductForSave = (data) => {

  const product = {
    ...data.info.values,
    metaFields: [],
    dataStreams: [],
    events: [],
    webDashboard: data.webDashboard
  };

  if (Array.isArray(data.events.fields) && Array.isArray(data.metadata.fields)) {
    data.events.fields.forEach((event) => {

      const transformIgnorePeriod = (event) => {
        event.ignorePeriod = moment.duration(moment(event.ignorePeriod).format('HH:mm')).asSeconds();
        return event;
      };

      const transformNotifications = (event, type) => {

        if (event[type]) {
          event[type] = event[type].map((contactId) => {
            const metadata = _.find(data.metadata.fields, {id: Number(contactId)});
            return {
              metaFieldId: Number(contactId),
              type: 'Contact',
              value: metadata.values.name
            };
          });
        }

        return {
          ...event
        };
      };

      let transformed = {
        type: event.type,
        ...event.values
      };

      transformed = transformNotifications(transformed, 'pushNotifications');
      transformed = transformNotifications(transformed, 'emailNotifications');
      transformed = transformIgnorePeriod(transformed, 'pushNotifications');

      product.events.push(transformed);

    });
  }

  if (Array.isArray(data.metadata.fields)) {
    data.metadata.fields.forEach((value) => {

      let values;
      if (value.type === Metadata.Fields.CONTACT) {
        values = prepareContactValuesForSave(value.values);
      } else {
        values = value.values;
      }


      const field = {
        id: value.id,
        name: value.name,
        type: value.type,
        ...values
      };

      product.metaFields.push(field);
    });
  }

  if (Array.isArray(data.dataStreams.fields)) {
    data.dataStreams.fields.forEach((value) => {
      product.dataStreams.push({
        id: value.id,
        ...value.values
      });
    });
  }

  return product;
};

const prepareContactValuesForSave = (values) => {
  return values;
};

const prepareContactValuesForEdit = (fields) => {

  const values = Object.assign({}, fields);

  return values;

};
