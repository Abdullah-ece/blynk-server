import React from 'react';
import {Form} from 'components/UI';
import {
  Default as OptionDefault,
  Input as DefinedInput
} from './components/Option';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import FieldStub from 'scenes/Products/components/FieldStub';

class ContactField extends BaseField.Static {

  DEFAULT_VALUE = 'No Value';

  static propTypes = {
    name: React.PropTypes.string,
    value: React.PropTypes.any
  };

  getPreviewValues() {
    const name = this.props.fields.name;
    let value = [];

    const values = ['email', 'phone', 'streetAddress', 'city', 'state', 'zip'];

    const placeholders = {
      firstName: 'First Name',
      lastName: 'Last Name',
      email: 'mail@example.com',
      phone: '+1 555 55 55',
      streetAddress: 'Street Address',
      city: 'City',
      state: 'State',
      zip: 'ZIP'
    };

    const checkIsFieldValid = (name) => {
      return this.props.fields.values[name].checked;
    };

    if (['firstName', 'lastName'].every(checkIsFieldValid)) {
      const firstName = this.props.fields.values.firstName.value || placeholders.firstName;
      const lastName = this.props.fields.values.lastName.value || placeholders.lastName;
      value.push(`${firstName}, ${lastName}`);
    } else {
      values.unshift('firstName', 'lastName');
    }

    values.forEach((field) => {
      if (checkIsFieldValid(field)) {
        if (this.props.fields.isDefaultsEnabled && this.props.fields.values[field].value) {
          value.push(this.props.fields.values[field].value);
        } else {
          value.push(placeholders[field]);
        }
      }
    });


    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value.length ? value.join('\n') : null,
      inline: !!value.length
    };
  }

  component() {

    return (
      <div>
        <Form.Item label="Contact" offset="normal">
          <FieldStub>
            { this.props.fields.name }
          </FieldStub>
        </Form.Item>

        <FormItem offset={false} visible={!!this.props.fields.isDefaultsEnabled}>

          <Form.Item label="Fields" offset="small">
            <DefinedInput.Static placeholder="First name" prefix="firstName"
                                 isChecked={this.props.fields.values.firstName.checked}
                                 value={this.props.fields.values.firstName.value}/>
            <DefinedInput.Static placeholder="Last name" prefix="lastName"
                                 isChecked={this.props.fields.values.lastName.checked}
                                 value={this.props.fields.values.lastName.value}/>

            <DefinedInput.Static placeholder="E-mail address" prefix="email"
                                 isChecked={this.props.fields.values.email.checked}
                                 value={this.props.fields.values.email.value}/>
            <DefinedInput.Static placeholder="Phone number" prefix="phone"
                                 isChecked={this.props.fields.values.phone.checked}
                                 value={this.props.fields.values.phone.value}/>
            <DefinedInput.Static placeholder="Street address" prefix="streetAddress"
                                 isChecked={this.props.fields.values.streetAddress.checked}
                                 value={this.props.fields.values.streetAddress.value}/>
            <DefinedInput.Static placeholder="City" prefix="city"
                                 isChecked={this.props.fields.values.city.checked}
                                 value={this.props.fields.values.city.value}/>
            <DefinedInput.Static placeholder="State" prefix="state"
                                 isChecked={this.props.fields.values.state.checked}
                                 value={this.props.fields.values.state.value}/>
            <DefinedInput.Static placeholder="ZIP Code" prefix="zip"
                                 isChecked={this.props.fields.values.zip.checked}
                                 value={this.props.fields.values.zip.value}/>
          </Form.Item>
        </FormItem>

        <FormItem offset={false} visible={!this.props.fields.isDefaultsEnabled}>

          <Form.Item label="Fields" offset="small">
            <OptionDefault.Static placeholder="First name" prefix="firstName"
                                  isChecked={this.props.fields.values.firstName.checked}
                                  value={this.props.fields.values.firstName.value}/>
            <OptionDefault.Static placeholder="Last name" prefix="lastName"
                                  isChecked={this.props.fields.values.lastName.checked}
                                  value={this.props.fields.values.lastName.value}/>

            <OptionDefault.Static placeholder="E-mail address" prefix="email"
                                  isChecked={this.props.fields.values.email.checked}
                                  value={this.props.fields.values.email.value}/>
            <OptionDefault.Static placeholder="Phone number" prefix="phone"
                                  isChecked={this.props.fields.values.phone.checked}
                                  value={this.props.fields.values.phone.value}/>
            <OptionDefault.Static placeholder="Street address" prefix="streetAddress"
                                  isChecked={this.props.fields.values.streetAddress.checked}
                                  value={this.props.fields.values.streetAddress.value}/>
            <OptionDefault.Static placeholder="City" prefix="city"
                                  isChecked={this.props.fields.values.city.checked}
                                  value={this.props.fields.values.city.value}/>
            <OptionDefault.Static placeholder="State" prefix="state"
                                  isChecked={this.props.fields.values.state.checked}
                                  value={this.props.fields.values.state.value}/>
            <OptionDefault.Static placeholder="ZIP Code" prefix="zip"
                                  isChecked={this.props.fields.values.zip.checked}
                                  value={this.props.fields.values.zip.value}/>
          </Form.Item>
        </FormItem>

        {/*<FormItem offset={false}>*/}
        {/*<FormItem.Title style={{width: '50%'}}>Contact</FormItem.Title>*/}
        {/*<FormItem.Content>*/}
        {/*<div className="product-metadata-static-field">*/}
        {/*{this.props.fields.name}*/}
        {/*</div>*/}
        {/*</FormItem.Content>*/}
        {/*</FormItem>*/}
      </div>
    );
  }
}

export default ContactField;
