import Base from '../Base';
import {Metadata, Currency, MetadataIconFieldName, MetadataInitialValues} from 'services/Products';

export default class AddMetadataFields extends Base {

  title = 'Metadata';

  fields = [
    {
      type: Metadata.Fields.TEXT,
      title: 'Text',
    },
    {
      type: Metadata.Fields.NUMBER,
      title: 'Number',
    },
    {
      type: Metadata.Fields.UNIT,
      title: 'Unit',
    },
    {
      type: Metadata.Fields.RANGE,
      title: 'Time Range',
    },
    {
      type: Metadata.Fields.CONTACT,
      title: 'Contact',
    },
    {
      type: Metadata.Fields.TIME,
      title: 'Time',
    },
    {
      type: Metadata.Fields.COST,
      title: 'Cost',
    },
    {
      type: Metadata.Fields.COORDINATES,
      title: 'Coordinates',
    },
    {
      type: Metadata.Fields.SWITCH,
      title: 'Switch',
    },
    {
      type: Metadata.Fields.DEVICE_REFERENCE,
      title: 'Device Reference',
    },
    {
      type: Metadata.Fields.LIST,
      title: 'List',
    },
    {
      type: Metadata.Fields.TEMPLATE_ID,
      title: 'Template Id',
    }
  ];

  typesPredefinedValues = {
    [Metadata.Fields.TEXT]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e9a2',
    },
    [Metadata.Fields.NUMBER]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e776',
    },
    [Metadata.Fields.UNIT]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e951',
    },
    [Metadata.Fields.RANGE]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e8e4',
    },
    [Metadata.Fields.CONTACT]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e71a',
    },
    [Metadata.Fields.TIME]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e8e8',
    },
    [Metadata.Fields.COORDINATES]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e77a',
    },
    [Metadata.Fields.SWITCH]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e670',
    },
    [Metadata.Fields.LIST]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e930',
    },
    [Metadata.Fields.DEVICE_REFERENCE]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e915',
    },
    [Metadata.Fields.TEMPLATE_ID]: {
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e6b3',
    },
    [Metadata.Fields.COST]: {
      values: {
        currency: Currency.USD.key
      },
      ...MetadataInitialValues,
      [MetadataIconFieldName]: 'e757',
    }
  };

}
