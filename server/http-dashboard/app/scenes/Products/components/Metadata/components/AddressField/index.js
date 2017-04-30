import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField} from 'components/Form';
import {formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import Static from './static';
import './styles.less';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      street: selector(state, 'street'),
      city: selector(state, 'city'),
      zip: selector(state, 'zip'),
      state: selector(state, 'state'),
      country: selector(state, 'country'),
    }
  };
})
class TextField extends BaseField {

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
    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Name</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Address (optional)</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField name="name" type="text" placeholder="Field Name" validate={[
              Validation.Rules.required
            ]}/>
            <MetadataFormField name="streetAddress" type="text" placeholder="Street"/>
          </Input.Group>
          <Input.Group compact className="address-group">
            <MetadataFormField className="address-field" name="city" type="text" placeholder="City"/>
            <MetadataFormField className="address-field" name="zip" type="text" placeholder="Zip"/>
            <MetadataFormField className="address-field" name="state" type="text" placeholder="State"/>
            <MetadataFormField className="address-field" name="country" type="text" placeholder="Country"/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }

}

TextField.Static = Static;
export default TextField;
