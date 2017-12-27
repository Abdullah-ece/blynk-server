import React from 'react';
import FormItem from 'components/FormItem';
import {Form} from 'components/UI';
import {Switch, Row, Col} from 'antd';
import {formValueSelector, Field, change} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import {
  Default as OptionDefault,
  Input as DefinedInput
} from './components/Option';
import './styles.less';
import Static from './static';
import _ from 'lodash';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    events: state.Product.edit.events.fields,
    fields: {
      name: selector(state, 'name') || "",
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
          checked: selector(state, 'isPhoneEnabled'),
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
}, (dispatch) => ({
  updateFormField: bindActionCreators(change, dispatch)
}))
class ContactField extends BaseField {

  constructor(props) {
    super(props);

    this.onUncheckEmail = this.onUncheckEmail.bind(this);
    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);
  }

  shouldComponentUpdate(nextProps) {
    return !_.isEqual(this.props, nextProps);
  }

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

  switch(props) {
    return (
      <Switch size="small" className="contact-field-allow-default-values-switch" checked={!!props.input.value}
              onChange={(value) => {
                props.input.onChange(value);
              }}/>
    );
  }

  onUncheckEmail() {
    this.removeContactFromEvents();
  }

  removeContactFromEvents() {
    let events = [...this.props.events];

    const updated = events.map((event) => {

      let emailNotifications = event.values.emailNotifications &&
        event.values.emailNotifications.filter(
          (id) => !(Number(id) === Number(this.props.id))
        );

      let pushNotifications = event.values.pushNotifications &&
        event.values.pushNotifications.filter(
          (id) => !(Number(id) === Number(this.props.id))
        );

      this.props.updateFormField(`event${event.id}`, `emailNotifications`, emailNotifications);
      this.props.updateFormField(`event${event.id}`, `pushNotifications`, pushNotifications);

      const updated = {
        ...event,
        values: {
          ...event.values,
          emailNotifications: emailNotifications,
          pushNotifications: pushNotifications
        }
      };
      return updated;
    });

    this.props.onEventsChange(updated);
  }

  isContactUsedOnEvents() {
    return this.props.events.some((event) => {
      return (event.values.emailNotifications && event.values.emailNotifications.some((id) => (Number(id) === Number(this.props.id)))) || (
          event.values.pushNotifications && event.values.pushNotifications.some((id) => (Number(id) === Number(this.props.id)))
        );
    });
  }

  component() {

    let popconfirmOptions = {};
    if (this.isContactUsedOnEvents()) {
      popconfirmOptions = {
        onUncheck: true,
        message: <div>This contact is used on some<br/> Events Notifications.<br/> Are you sure?</div>,
        onConfirm: this.onUncheckEmail
      };
    }

    return (
      <div>
        <Form.Item label="Contact" offset="extra-small">
          <Form.Input validateOnBlur={true}
                      className={`metadata-name-field-${this.props.field.id} metadata-contact-field`}
                      name="name" type="text"
                      onFocus={this.onFocus} onBlur={this.onBlur}
                      placeholder="Field Name"
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
                              isChecked={this.props.fields.values.firstName.checked}
                              onFocus={this.onFocus} onBlur={this.onBlur}
                              value={this.props.fields.values.firstName.value}/>
                <DefinedInput placeholder="Last name" prefix="lastName"
                              isChecked={this.props.fields.values.lastName.checked}
                              onFocus={this.onFocus} onBlur={this.onBlur}
                              value={this.props.fields.values.lastName.value}/>
              </Form.Items>
            </Col>
            <Col span={12}>
              <Form.Items offset="small">
                <DefinedInput placeholder="E-mail address" prefix="email"
                              onFocus={this.onFocus} onBlur={this.onBlur}
                              isChecked={this.props.fields.values.email.checked}
                              value={this.props.fields.values.email.value}
                              popconfirm={popconfirmOptions}/>
                <DefinedInput placeholder="Phone number" prefix="phone"
                              onFocus={this.onFocus} onBlur={this.onBlur}
                              isChecked={this.props.fields.values.phone.checked}
                              value={this.props.fields.values.phone.value}/>
                <DefinedInput placeholder="Street address" prefix="streetAddress"
                              onFocus={this.onFocus} onBlur={this.onBlur}
                              isChecked={this.props.fields.values.streetAddress.checked}
                              value={this.props.fields.values.streetAddress.value}/>
                <DefinedInput placeholder="City" prefix="city"
                              onFocus={this.onFocus} onBlur={this.onBlur}
                              isChecked={this.props.fields.values.city.checked}
                              value={this.props.fields.values.city.value}/>
                <DefinedInput placeholder="State" prefix="state"
                              onFocus={this.onFocus} onBlur={this.onBlur}
                              isChecked={this.props.fields.values.state.checked}
                              value={this.props.fields.values.state.value}/>
                <DefinedInput placeholder="ZIP Code" prefix="zip"
                              onFocus={this.onFocus} onBlur={this.onBlur}
                              isChecked={this.props.fields.values.zip.checked}
                              value={this.props.fields.values.zip.value}/>
              </Form.Items>
            </Col>
          </Row>
        </FormItem>


        <FormItem offset={false} visible={!this.props.fields.isDefaultsEnabled}>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Items offset="small">
                <OptionDefault placeholder="First name" prefix="firstName"
                               isChecked={this.props.fields.values.firstName.checked}
                               value={this.props.fields.values.firstName.value}/>
                <OptionDefault placeholder="Last name" prefix="lastName"
                               isChecked={this.props.fields.values.lastName.checked}
                               value={this.props.fields.values.lastName.value}/>
              </Form.Items>
            </Col>
            <Col span={12}>
              <Form.Items offset="small">
                <OptionDefault placeholder="E-mail address" prefix="email"
                               isChecked={this.props.fields.values.email.checked}
                               value={this.props.fields.values.email.value}
                               popconfirm={popconfirmOptions}
                />

                < OptionDefault placeholder="Phone number" prefix="phone"
                                isChecked={this.props.fields.values.phone.checked}
                                value={this.props.fields.values.phone.value}/>

                <OptionDefault placeholder="Street address" prefix="streetAddress"
                               isChecked={this.props.fields.values.streetAddress.checked}
                               value={this.props.fields.values.streetAddress.value}/>

                <OptionDefault placeholder="City" prefix="city"
                               isChecked={this.props.fields.values.city.checked}
                               value={this.props.fields.values.city.value}/>

                <OptionDefault placeholder="State" prefix="state"
                               isChecked={this.props.fields.values.state.checked}
                               value={this.props.fields.values.state.value}/>

                <OptionDefault placeholder="ZIP Code" prefix="zip"
                               isChecked={this.props.fields.values.zip.checked}
                               value={this.props.fields.values.zip.value}/>
              </Form.Items>
            </Col>
          </Row>
        </FormItem>

      </div>
    );
  }
}

ContactField.Static = Static;

export default ContactField;
