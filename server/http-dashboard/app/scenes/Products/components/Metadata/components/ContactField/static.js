import React from 'react';
import {Form} from 'components/UI';
import {
  Default as OptionDefault,
  Input as DefinedInput
} from './components/Option';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';

class ContactField extends BaseField.Static {

  DEFAULT_VALUE = 'No Value';

  static propTypes = {
    name: React.PropTypes.string,
    value: React.PropTypes.any
  };

  getPreviewValues() {
    const name = this.props.fields.name;
    let value = [];

    const regex = /^(.*){1,}$/;

    const values = ['email', 'phone', 'streetAddress', 'city', 'state', 'zip'];

    const checkIsFieldValid = (name) => {
      return this.props.fields.values[name].checked
        && regex.test(this.props.fields.values[name].value)
        && this.props.fields.values[name].value
        && this.props.fields.values[name].value.trim();
    };

    if (['firstName', 'lastName'].every(checkIsFieldValid)) {
      value.push(`${this.props.fields.values.firstName.value}, ${this.props.fields.values.lastName.value}`);
    } else {
      values.unshift('firstName', 'lastName');
    }

    values.forEach((field) => {
      if (checkIsFieldValid(field)) {
        value.push(this.props.fields.values[field].value);
      }
    });

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: this.props.fields.isDefaultsEnabled && value.length ? value.join('\n') : null,
      inline: !!this.props.fields.isDefaultsEnabled && !!value.length
    };
  }

  component() {

    return (
      <div>
        <Form.Item label="Contact" offset="normal">
          <div className="product-metadata-static-field">
            { this.props.fields.name }
          </div>
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
