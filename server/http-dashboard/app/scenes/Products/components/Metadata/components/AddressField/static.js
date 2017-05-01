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
    const name = this.props.name;
    const address = [];
    if (this.props.streetAddress) {
      address.push(this.props.streetAddress);
    }
    if (this.props.city) {
      address.push(this.props.city);
    }
    if (this.props.zip) {
      address.push(this.props.zip);
    }
    if (this.props.state) {
      address.push(this.props.state);
    }
    if (this.props.country) {
      address.push(this.props.country);
    }

    return {
      name: name && typeof name === 'string' ? `${name.trim()}:` : null,
      value: address.length > 0 ? address.join(', ') : null
    };
  }

  component() {

    const streetAddressClassNames = classnames({
      'product-metadata-static-field': true,
      'no-value': !this.props.streetAddress
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
            {this.props.city && <div className="product-metadata-static-field">
              {this.props.city}
            </div>}
            {this.props.zip && <div className="product-metadata-static-field">
              {this.props.zip}
            </div>}
            {this.props.state && <div className="product-metadata-static-field">
              {this.props.state}
            </div>}
            {this.props.country && <div className="product-metadata-static-field">
              {this.props.country}
            </div>}
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default AddressField;
