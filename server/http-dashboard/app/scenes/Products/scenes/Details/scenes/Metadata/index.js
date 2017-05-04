import React from 'react';
import MetadataComponents from 'scenes/Products/components/Metadata';
import {Metadata as MetadataFields} from 'services/Products';
const {ItemsList, Fields: {TextField, NumberField, UnitField, TimeField, ShiftField, CostField, CoordinatesField, AddressField}} = MetadataComponents;
class Metadata extends React.Component {

  static propTypes = {
    product: React.PropTypes.shape({
      metaFields: React.PropTypes.array
    }),
  };

  getFields() {

    const fields = [];

    if (!this.props.product.metaFields)
      return fields;

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

      if (field.type === MetadataFields.Fields.ADDRESS) {
        fields.push(
          <AddressField.Static
            {...props}
            name={field.name}
            role={field.role}
            streetAddress={field.streetAddress}
            city={field.city}
            state={field.state}
            zip={field.zip}
            country={field.country}
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

      if (field.type === MetadataFields.Fields.RANGE) {
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
            price={field.price}
            perValue={field.perValue}
            currency={field.currency}
            units={field.units}
            role={field.role}
          />
        );
      }

      if (field.type === MetadataFields.Fields.COORDINATES) {
        fields.push(
          <CoordinatesField.Static
            {...props}
            name={field.name}
            lat={field.lat}
            lon={field.lon}
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
