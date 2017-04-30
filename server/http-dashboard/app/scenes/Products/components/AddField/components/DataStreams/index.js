import Base from '../Base';
import {Metadata, Currency} from 'services/Products';

export default class AddMetadataFields extends Base {

  title = 'Metadata';

  fields = [
    {
      type: Metadata.Fields.TEXT,
      title: 'Text'
    },
    {
      type: Metadata.Fields.NUMBER,
      title: 'Number'
    },
    {
      type: Metadata.Fields.UNIT,
      title: 'Unit'
    },
    {
      type: Metadata.Fields.TIME,
      title: 'Time'
    },
    {
      type: Metadata.Fields.SHIFT,
      title: 'Shift'
    },
    {
      type: Metadata.Fields.COST,
      title: 'Cost'
    },
    {
      type: Metadata.Fields.COORDINATES,
      title: 'Coordinates'
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
