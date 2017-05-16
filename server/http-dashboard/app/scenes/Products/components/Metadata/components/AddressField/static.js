import React from 'react';
import {Input} from 'antd';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import FieldStub from 'scenes/Products/components/FieldStub';

class AddressField extends BaseField.Static {

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
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: address.length > 0 ? address.join(', ') : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Name</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Address (optional)</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <FieldStub style={{width: '50%'}}>
              {this.props.name}
            </FieldStub>
            <FieldStub style={{width: '50%'}}>
              {this.props.streetAddress || this.DEFAULT_VALUE}
            </FieldStub>
          </Input.Group>
          <Input.Group compact className="address-group">
            {this.props.city && (
              <FieldStub>
                {this.props.city}
              </FieldStub>
            )}
            {this.props.zip && (
              <FieldStub>
                {this.props.zip}
              </FieldStub>
            )}
            {this.props.state && (
              <FieldStub>
                {this.props.state}
              </FieldStub>
            )}
            {this.props.country && (
              <FieldStub>
                {this.props.country}
              </FieldStub>
            )}
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default AddressField;
