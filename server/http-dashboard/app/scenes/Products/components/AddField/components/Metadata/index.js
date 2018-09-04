import Base from '../Base';
import {Metadata, Currency, MetadataIconFieldName, MetadataInitialValues} from 'services/Products';

export default class AddMetadataFields extends Base {

  title = 'Metadata';

  fields = [
    {
      type: Metadata.Fields.TEXT,
      title: 'Text',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'text-size',
    },
    {
      type: Metadata.Fields.NUMBER,
      title: 'Number',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'dial',
    },
    {
      type: Metadata.Fields.UNIT,
      title: 'Unit',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'expand3',
    },
    {
      type: Metadata.Fields.RANGE,
      title: 'Time Range',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'history2',
    },
    {
      type: Metadata.Fields.CONTACT,
      title: 'Contact',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'contacts',
    },
    {
      type: Metadata.Fields.TIME,
      title: 'Time',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'clock3',
    },
    {
      type: Metadata.Fields.COST,
      title: 'Cost',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'receipt',
    },
    {
      type: Metadata.Fields.COORDINATES,
      title: 'Coordinates',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'map-marker',
    },
    {
      type: Metadata.Fields.SWITCH,
      title: 'Switch',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'toggle-off',
    },
    {
      type: Metadata.Fields.DEVICE_REFERENCE,
      title: 'Device Reference',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'link',
    },
    {
      type: Metadata.Fields.LIST,
      title: 'List',
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'list4',
    }
  ];

  typesPredefinedValues = {
    [Metadata.Fields.COST]: {
      values: {
        currency: Currency.USD.key
      }
    }
  };

}
