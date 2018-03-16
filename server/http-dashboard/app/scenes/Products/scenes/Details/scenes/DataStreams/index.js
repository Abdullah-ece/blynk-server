import React from 'react';
// import {BackTop} from 'components';
import {
  DataStreamsItemsList,
  DataStreamsBaseField
} from 'scenes/Products/components/DataStreams';

class DataStreams extends React.Component {

  static propTypes = {
    product: React.PropTypes.shape({
      dataStreams: React.PropTypes.array
    }),
  };

  getFields() {

    const fields = [];

    if (!this.props.product.dataStreams)
      return fields;

    this.props.product.dataStreams.forEach((field, key) => {

      const props = {
        key: key,
        label: field.label,
        units: field.units,
        min: field.min,
        max: field.max,
        pin: field.pin
      };

      fields.push(
        <DataStreamsBaseField.Static
          {...props}
        />
      );

    });

    return fields;

  }

  render() {

    if (!this.getFields().length) return (
      <div className="product-no-fields">No Data Streams fields</div>
    );

    return (
      <DataStreamsItemsList static={true}>
        { this.getFields() }
        {/*<BackTop/>*/}
      </DataStreamsItemsList>
    );
  }
}

export default DataStreams;
