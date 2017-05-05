import React from 'react';
import FormItem from 'components/FormItem';
import {Form} from 'components/UI';
import {Switch, Row, Col} from 'antd';
import {reduxForm, formValueSelector, Field} from 'redux-form';
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
      allowDefaults: selector(state, 'allowDefaults'),
      fieldAvailable: selector(state, 'fieldAvailable'),
      isChecked: {
        firstName: selector(state, 'firstNameCheck'),
        lastName: selector(state, 'lastNameCheck'),
        email: selector(state, 'emailCheck'),
        phone: selector(state, 'phoneCheck'),
        address: selector(state, 'addressCheck'),
        city: selector(state, 'cityCheck'),
        state: selector(state, 'statCheck'),
        zip: selector(state, 'zipCheck'),
      },
      values: {
        firstName: selector(state, 'firstNameInput'),
        lastName: selector(state, 'lastNameInput'),
        email: selector(state, 'emailInput'),
        phone: selector(state, 'phoneInput'),
        address: selector(state, 'addressInput'),
        city: selector(state, 'cityInput'),
        state: selector(state, 'statInput'),
        zip: selector(state, 'zipInput'),
      }
    }
  };
})
@reduxForm({
  touchOnChange: true
})
export default class ContactField extends BaseField {

  getPreviewValues() {
    const name = this.props.fields.name;
    let value = [];

    const regex = /^(.*){1,}$/;

    const values = ['email', 'phone', 'address', 'city', 'stat', 'zip'];

    const checkIsFieldValid = (name) => {
      return this.props.fields.isChecked[name]
        && regex.test(this.props.fields.values[name])
        && this.props.fields.values[name]
        && this.props.fields.values[name].trim();
    };

    if (checkIsFieldValid('firstName') && checkIsFieldValid('lastName')) {
      value.push(`${this.props.fields.values.firstName}, ${this.props.fields.values.lastName}`);
    } else {
      values.unshift('firstName', 'lastName');
    }

    values.forEach((field) => {
      if (checkIsFieldValid(field)) {
        value.push(this.props.fields.values[field]);
      }
    });

    return {
      name: name && typeof name === 'string' ? `${name.trim()}:` : null,
      value: this.props.fields.allowDefaults && value.length ? value.join('\n') : null,
      inline: this.props.fields.allowDefaults && value.length
    };
  }

  switch(props) {
    return (
      <Switch size="small" className="contact-field-allow-default-values-switch" value={props.input.value}
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
                        Validation.Rules.required
                      ]}/>
        </Form.Item>
        <Form.Item offset="small" checkbox={true}>
          <div>
            <Field name="allowDefaults"
                   component={this.switch}/>
            <span className="contact-field-allow-default-values-title">Allow default values</span>
          </div>
        </Form.Item>
        { this.props.fields.allowDefaults && (
          <FormItem offset={false}>
            <Row gutter={8}>
              <Col span={12}>
                <Form.Items offset="small">
                  <DefinedInput placeholder="First name" prefix="firstName"
                                isChecked={this.props.fields.isChecked.firstName}/>
                  <DefinedInput placeholder="Last name" prefix="lastName"
                                isChecked={this.props.fields.isChecked.lastName}/>
                </Form.Items>
              </Col>
              <Col span={12}>
                <Form.Items offset="small">
                  <DefinedInput placeholder="E-mail address" prefix="email"
                                isChecked={this.props.fields.isChecked.email}/>
                  <DefinedInput placeholder="Phone number" prefix="phone"
                                isChecked={this.props.fields.isChecked.phone}/>
                  <DefinedInput placeholder="Street address" prefix="address"
                                isChecked={this.props.fields.isChecked.address}/>
                  <DefinedInput placeholder="City" prefix="city" isChecked={this.props.fields.isChecked.city}/>
                  <DefinedInput placeholder="State" prefix="state" isChecked={this.props.fields.isChecked.state}/>
                  <DefinedInput placeholder="ZIP Code" prefix="zip" isChecked={this.props.fields.isChecked.zip}/>
                </Form.Items>
              </Col>
            </Row>
          </FormItem>
        )}


        { !this.props.fields.allowDefaults && (
          <FormItem offset={false}>
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
                  <OptionDefault placeholder="Street address" prefix="address"/>
                  <OptionDefault placeholder="City" prefix="city"/>
                  <OptionDefault placeholder="State" prefix="state"/>
                  <OptionDefault placeholder="ZIP Code" prefix="zip"/>
                </Form.Items>
              </Col>
            </Row>
          </FormItem>
        )}

      </div>
    );
  }
}
