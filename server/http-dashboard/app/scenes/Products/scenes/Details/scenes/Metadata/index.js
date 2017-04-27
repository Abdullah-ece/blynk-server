import React from 'react';
import MetadataComponents from 'scenes/Products/components/Metadata';
import {Metadata as MetadataFields} from 'services/Products';
const {ItemsList, Fields: {TextField}} = MetadataComponents;
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
