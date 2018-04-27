import _ from 'lodash';
import moment from 'moment';
import 'moment-duration-format';
import {Roles} from '../Roles';
import {
  DEFAULT_HARDWARE_TYPE,
  DEFAULT_CONNECTION_TYPE,
} from 'services/Devices';

import {List, fromJS} from 'immutable';

export const TABS = {
  INFO: 'info',
  METADATA: 'metadata',
  DATA_STREAMS: 'datastreams',
  EVENTS: 'events',
  DASHBOARD: 'dashboard'
};

export const getNextId = (items = []) => {
  if(!(items instanceof List))
    items = fromJS(items);

  return items.reduce((acc, item) => Number(item.get('id')) > acc ? Number(item.get('id')) : acc, 0) + 1;
};

export const FORMS = {
  PRODUCTS_PRODUCT_MANAGE: 'products-product-manage',
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
  CRITICAL: 'CRITICAL',
  WAS_OFFLINE: 'WAS_OFFLINE'
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
    CONTACT: 'Contact',
    DEVICE_REFERENCE: 'DeviceReference',
    LIST: 'List'
  }
};

export const hardcodedRequiredMetadataFieldsNames = {
  DeviceName: 'Device Name',
  DeviceOwner: 'Device Owner',
  LocationName: 'Location Name',
  Manufacturer: 'Manufacturer',
  ModelName: 'Model Name',
  TimezoneOfTheDevice: 'Device Timezone'
};

export const hardcodedRequiredMetadataFieldsNamesList = [
  hardcodedRequiredMetadataFieldsNames.DeviceName,
  hardcodedRequiredMetadataFieldsNames.DeviceOwner,
  hardcodedRequiredMetadataFieldsNames.LocationName,
  hardcodedRequiredMetadataFieldsNames.Manufacturer,
  hardcodedRequiredMetadataFieldsNames.ModelName,
  hardcodedRequiredMetadataFieldsNames.TimezoneOfTheDevice
];

export const filterMetadataFields = (fields, filterHardcoded = true) => {

  return fields.filter((field) => {
    if(filterHardcoded)
      return hardcodedRequiredMetadataFieldsNamesList.indexOf(field.get('name')) >= 0;

    return hardcodedRequiredMetadataFieldsNamesList.indexOf(field.get('name')) === -1;
  });
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
      role: Roles.USER.value,
    },
    {
      id: 2,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.DeviceOwner,
      role: Roles.USER.value,
    },
    {
      id: 3,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.LocationName,
      role: Roles.STAFF.value,
    },
    {
      id: 4,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.Manufacturer,
      value: manufacturerDefaultValue,
      role: Roles.SUPER_ADMIN.value,
    },
    {
      id: 5,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.ModelName,
      role: Roles.STAFF.value,
    },
    {
      id: 6,
      type: Metadata.Fields.TEXT,
      name: hardcodedRequiredMetadataFieldsNames.TimezoneOfTheDevice,
      value: timezoneDefaultValue || null,
      role: Roles.USER.value,
    }
  ];
};

export const PRODUCT_CREATE_INITIAL_VALUES = ({timezoneDefaultValue, manufacturerDefaultValue}) => ({
  name: '',
  boardType: DEFAULT_HARDWARE_TYPE,
  connectionType: DEFAULT_CONNECTION_TYPE,
  description: '',
  logoUrl: '',
  metaFields: [
    ...getHardcodedRequiredMetadataFields({timezoneDefaultValue, manufacturerDefaultValue}),
  ],
  dataStreams: [],
  events: [
    {
      id: 1,
      type: EVENT_TYPES.ONLINE
    },
    {
      id: 2,
      type: EVENT_TYPES.OFFLINE,
      ignorePeriod: 0,
    }
  ]
});

export const exampleMetadataField = {
  type: Metadata.Fields.TEXT,
  name: 'Example: Serial Number',
  role: 'ADMIN'
};

export const Unit = {
  None: {
    abbreviation: '',
    key: 'None',
    value: 'None'
  },
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
  Tonne: {
    abbreviation: 't',
    key: 'Tonne',
    value: 'Tonne'
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
    abbreviation: 'l',
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
    abbreviation: 'K',
    key: 'Kelvin',
    value: 'Kelvin'
  },
  Percentage: {
    abbreviation: '%',
    key: 'Percentage',
    value: 'Percentage'
  },
  RPM: {
    abbreviation: 'rpm',
    key: 'RPM',
    value: 'RPM'
  },
  Year: {
    abbreviation: 'yr',
    key: 'Year',
    value: 'Year'
  },
  Month: {
    abbreviation: 'mo',
    key: 'Month',
    value: 'Month'
  },
  Week: {
    abbreviation: 'wk',
    key: 'Week',
    value: 'Week'
  },
  Day: {
    abbreviation: 'd',
    key: 'Day',
    value: 'Day'
  },
  Hour: {
    abbreviation: 'hr',
    key: 'Hour',
    value: 'Hour'
  },
  Minute: {
    abbreviation: 'min',
    key: 'Minute',
    value: 'Minute'
  },
  Second: {
    abbreviation: 's',
    key: 'Second',
    value: 'Second'
  },

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

      let field = {};

      if (values.hardcoded)
        field.isDefault = true;

      field = {
        ...field,
        id: value.id,
        name: value.name,
        type: value.type,
        ...values,
      };

      delete field.hardcoded;
      delete field.isSavedBefore;

      product.metaFields.push(field);
    });
  }

  if (Array.isArray(data.dataStreams.fields)) {
    data.dataStreams.fields.forEach((value) => {

      const values = {
        ...value.values,
      };

      delete values.tableDescriptor;

      product.dataStreams.push({
        id: value.id,
        ...values
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

export const isDataStreamPristine = (field) => (
  !field.get('label') && !field.get('min') && !field.get('max') && field.get('units') === Unit.None.key
);

export const isEventPristine = (field) => (
  !field.get('name') && !field.get('eventCode') &&
  !field.get('description') && !field.get('isNotificationsEnabled') &&
  (!field.get('emailNotifications') || !field.get('emailNotifications').size) &&
  (!field.get('pushNotifications') || !field.get('pushNotifications').size)
);

export const isMetadataPristine = () => ({
  [Metadata.Fields.TEXT]: (field) => (
    !field.get('name') && !field.get('value')
  ),
  [Metadata.Fields.NUMBER]: (field) => (
    !field.get('name') && !field.get('value')
  ),
  [Metadata.Fields.COST]: (field) => (
    !field.get('name') && !field.get('perValue') && !field.get('price') && !field.get('units') && !field.get('currency')
  ),
  [Metadata.Fields.TIME]: (field) => (
    !field.get('name') && !field.get('time')
  ),
  [Metadata.Fields.RANGE]: (field) => (
    !field.get('name') && !field.get('from') && !field.get('to')
  ),
  [Metadata.Fields.SWITCH]: (field) => (
    !field.get('name') && !field.get('from') && !field.get('to')
  ),
  [Metadata.Fields.COORDINATES]: (field) => (
    !field.get('name') && !field.get('lat') && !field.get('lon')
  ),
  [Metadata.Fields.UNIT]: (field) => (
    !field.get('name') && !field.get('value') && !field.get('units')
  ),
  [Metadata.Fields.DEVICE_REFERENCE]: (field) => (
    !field.get('name') && !field.get('selectedProductIds')
  ),
  [Metadata.Fields.LIST]: (field) => (
    !field.get('name') && !field.get('options')
  ),
  [Metadata.Fields.CONTACT]: (field) => (
    !field.get('name') && !field.get('isFirstNameEnabled') && !field.get('firstName') && !field.get('isLastNameEnabled') && !field.get('lastName') &&
    !field.get('isEmailEnabled') && !field.get('email') && !field.get('isPhoneEnabled') && !field.get('phone') &&
    !field.get('isStreetAddressEnabled') && !field.get('streetAddress') && !field.get('isCityEnabled') && !field.get('city') &&
    !field.get('isStateEnabled') && !field.get('state') && !field.get('isZipEnabled') && !field.get('zip')
  ),

});
