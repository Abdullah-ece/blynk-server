import React from 'react';
import MetadataComponents from 'scenes/Products/components/Metadata';
import {Metadata as MetadataFields} from 'services/Products';
const {ItemsList, Fields: {TextField, NumberField, UnitField, TimeField, ShiftField, CostField}} = MetadataComponents;
class Metadata extends React.Component {

  static propTypes = {
    product: React.PropTypes.shape({
      metaFields: React.PropTypes.array
    }),
  };

  getFields() {

    const fields = [];

    this.props.product.metaFields.forEach((field, key) => {

      const props = {
        key: key
      };

      if (field.type === MetadataFields.Fields.TEXT) {
        fields.push(
          <TextField.Static
            {...props}
            name={field.name}
            value={field.value}
            role={field.role}
          />
        );
      }

      if (field.type === MetadataFields.Fields.NUMBER) {
        fields.push(
          <NumberField.Static
            {...props}
            name={field.name}
            value={field.value}
            role={field.role}
          />
        );
      }

      if (field.type === MetadataFields.Fields.UNIT) {
        fields.push(
          <UnitField.Static
            {...props}
            name={field.name}
            value={field.value}
            units={field.units}
            role={field.role}
          />
        );
      }

      if (field.type === MetadataFields.Fields.TIME) {
        fields.push(
          <TimeField.Static
            {...props}
            name={field.name}
            time={field.time}
            role={field.role}
          />
        );
      }

      if (field.type === MetadataFields.Fields.SHIFT) {
        fields.push(
          <ShiftField.Static
            {...props}
            name={field.name}
            from={field.from}
            to={field.to}
            role={field.role}
          />
        );
      }

      if (field.type === MetadataFields.Fields.COST) {
        fields.push(
          <CostField.Static
            {...props}
            name={field.name}
            value={field.value}
            currency={field.currency}
            role={field.role}
          />
        );
      }

    });

    return fields;

  }

  render() {
    return (
      <ItemsList static={true}>
        { this.getFields() }
      </ItemsList>
    );
  }
}

export default Metadata;
