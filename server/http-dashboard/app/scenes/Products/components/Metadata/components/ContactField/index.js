import React from 'react';
import FormItem from 'components/FormItem';
import {Form} from 'components/UI';
import {Switch, Row, Col} from 'antd';
import {formValueSelector, Field} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import {
  Default as OptionDefault,
  Input as DefinedInput
} from './components/Option';
import './styles.less';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      isDefaultsEnabled: selector(state, 'isDefaultsEnabled'),
      fieldAvailable: selector(state, 'fieldAvailable'),
      values: {
        firstName: {
          checked: selector(state, 'isFirstNameEnabled'),
          value: selector(state, 'firstName'),
        },
        lastName: {
          checked: selector(state, 'isLastNameEnabled'),
          value: selector(state, 'lastName'),
        },
        email: {
          checked: selector(state, 'isEmailEnabled'),
          value: selector(state, 'email'),
        },
        phone: {
          checked: selector(state, 'phoneInput'),
          value: selector(state, 'phone'),
        },
        streetAddress: {
          checked: selector(state, 'isStreetAddressEnabled'),
          value: selector(state, 'streetAddress'),
        },
        city: {
          checked: selector(state, 'isCityEnabled'),
          value: selector(state, 'city'),
        },
        state: {
          checked: selector(state, 'isStateEnabled'),
          value: selector(state, 'state'),
        },
        zip: {
          checked: selector(state, 'isZipEnabled'),
          value: selector(state, 'zip'),
        }
      }
    }
  };
})
export default class ContactField extends BaseField {

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

  switch(props) {
    return (
      <Switch size="small" className="contact-field-allow-default-values-switch" checked={!!props.input.value}
              onChange={(value) => {
                props.input.onChange(value);
              }}/>
    );
  }

  component() {

    return (
      <div>
        <Form.Item label="Contact" offset="extra-small">
          <Form.Input className="metadata-contact-field" name="name" type="text" placeholder="Field Name"
                      validate={[
                        Validation.Rules.required, Validation.Rules.metafieldName,
                      ]}/>
        </Form.Item>
        <Form.Item offset="small" checkbox={true}>
          <div>
            <Field name="isDefaultsEnabled"
                   component={this.switch}/>
            <span className="contact-field-allow-default-values-title">Allow default values</span>
          </div>
        </Form.Item>

        <FormItem offset={false} visible={!!this.props.fields.isDefaultsEnabled}>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Items offset="small">
                <DefinedInput placeholder="First name" prefix="firstName"
                              isChecked={this.props.fields.values.firstName.checked}/>
                <DefinedInput placeholder="Last name" prefix="lastName"
                              isChecked={this.props.fields.values.lastName.checked}/>
              </Form.Items>
            </Col>
            <Col span={12}>
              <Form.Items offset="small">
                <DefinedInput placeholder="E-mail address" prefix="email"
                              isChecked={this.props.fields.values.email.checked}/>
                <DefinedInput placeholder="Phone number" prefix="phone"
                              isChecked={this.props.fields.values.phone.checked}/>
                <DefinedInput placeholder="Street address" prefix="streetAddress"
                              isChecked={this.props.fields.values.streetAddress.checked}/>
                <DefinedInput placeholder="City" prefix="city"
                              isChecked={this.props.fields.values.city.checked}/>
                <DefinedInput placeholder="State" prefix="state"
                              isChecked={this.props.fields.values.state.checked}/>
                <DefinedInput placeholder="ZIP Code" prefix="zip"
                              isChecked={this.props.fields.values.zip.checked}/>
              </Form.Items>
            </Col>
          </Row>
        </FormItem>


        <FormItem offset={false} visible={!this.props.fields.isDefaultsEnabled}>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Items offset="small">
                <OptionDefault placeholder="First name" prefix="firstName"/>
                <OptionDefault placeholder="Last name" prefix="lastName"/>
              </Form.Items>
            </Col>
            <Col span={12}>
              <Form.Items offset="small">
                <OptionDefault placeholder="E-mail address" prefix="email"/>
                <OptionDefault placeholder="Phone number" prefix="phone"/>
                <OptionDefault placeholder="Street address" prefix="streetAddress"/>
                <OptionDefault placeholder="City" prefix="city"/>
                <OptionDefault placeholder="State" prefix="state"/>
                <OptionDefault placeholder="ZIP Code" prefix="zip"/>
              </Form.Items>
            </Col>
          </Row>
        </FormItem>

      </div>
    );
  }
}
