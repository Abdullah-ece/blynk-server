import React from 'react';
import {Input} from 'antd';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import classnames from 'classnames';

class AddressField extends BaseField.Static {

  DEFAULT_VALUE = 'No Value';

  static propTypes = {
    name: React.PropTypes.string,
    value: React.PropTypes.any
  };

  getPreviewValues() {
    const name = this.props.fields.name;
    const address = [];
    if (this.props.fields.streetAddress) {
      address.push(this.props.fields.streetAddress);
    }
    if (this.props.fields.city) {
      address.push(this.props.fields.city);
    }
    if (this.props.fields.zip) {
      address.push(this.props.fields.zip);
    }
    if (this.props.fields.state) {
      address.push(this.props.fields.state);
    }
    if (this.props.fields.country) {
      address.push(this.props.fields.country);
    }

    return {
      name: name && typeof name === 'string' ? `${name.trim()}:` : null,
      value: address.length > 0 ? address.join(', ') : null
    };
  }

  component() {

    const streetAddressClassNames = classnames({
      'product-metadata-static-field': true,
      'no-value': !this.props.value
    });
    const cityClassNames = classnames({
          'product-metadata-static-field': true,
          'no-value': !this.props.value
        });
    const zipClassNames = classnames({
          'product-metadata-static-field': true,
          'no-value': !this.props.value
        });
    const stateClassNames = classnames({
          'product-metadata-static-field': true,
          'no-value': !this.props.value
        });
    const countryClassNames = classnames({
          'product-metadata-static-field': true,
          'no-value': !this.props.value
        });

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Name</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Address (optional)</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <div className="product-metadata-static-field">
              {this.props.name}
            </div>
            <div className={streetAddressClassNames}>
              {this.props.streetAddress || this.DEFAULT_VALUE}
            </div>
          </Input.Group>
          <Input.Group compact className="address-group">
            <div className={cityClassNames}>
              {this.props.city || this.DEFAULT_VALUE}
            </div>
            <div className={zipClassNames}>
              {this.props.zip || this.DEFAULT_VALUE}
            </div>
            <div className={stateClassNames}>
              {this.props.state || this.DEFAULT_VALUE}
            </div>
            <div className={countryClassNames}>
              {this.props.country || this.DEFAULT_VALUE}
            </div>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default AddressField;
