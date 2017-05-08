import React from 'react';
import {Col, Row} from 'antd';
import {Form} from 'components/UI';
import {
  Default as OptionDefault
} from './components/Option';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';

class AddressField extends BaseField.Static {

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
      return this.props.fields[name].checked
        && regex.test(this.props.fields[name].value)
        && this.props.fields[name].value
        && this.props.fields[name].value.trim();
    };

    if (['firstName', 'lastName'].every(checkIsFieldValid)) {
      value.push(`${this.props.fields.firstName.value}, ${this.props.fields.lastName.value}`);
    } else {
      values.unshift('firstName', 'lastName');
    }

    values.forEach((field) => {
      if (checkIsFieldValid(field)) {
        value.push(this.props.fields[field].value);
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
        <Form.Item label="Contact" offset="extra-small">
          <div className="product-metadata-static-field">
            { this.props.fields.name }
          </div>
        </Form.Item>

        {/*<FormItem offset={false} visible={!!this.props.fields.isDefaultsEnabled}>*/}
        {/*<Row gutter={8}>*/}
        {/*<Col span={12}>*/}
        {/*<Form.Items offset="small">*/}
        {/*<DefinedInput placeholder="First name" prefix="firstName"*/}
        {/*isChecked={this.props.fields.values.firstName.checked}/>*/}
        {/*<DefinedInput placeholder="Last name" prefix="lastName"*/}
        {/*isChecked={this.props.fields.values.lastName.checked}/>*/}
        {/*</Form.Items>*/}
        {/*</Col>*/}
        {/*<Col span={12}>*/}
        {/*<Form.Items offset="small">*/}
        {/*<DefinedInput placeholder="E-mail address" prefix="email"*/}
        {/*isChecked={this.props.fields.values.email.checked}/>*/}
        {/*<DefinedInput placeholder="Phone number" prefix="phone"*/}
        {/*isChecked={this.props.fields.values.phone.checked}/>*/}
        {/*<DefinedInput placeholder="Street address" prefix="streetAddress"*/}
        {/*isChecked={this.props.fields.values.streetAddress.checked}/>*/}
        {/*<DefinedInput placeholder="City" prefix="city"*/}
        {/*isChecked={this.props.fields.values.city.checked}/>*/}
        {/*<DefinedInput placeholder="State" prefix="state"*/}
        {/*isChecked={this.props.fields.values.state.checked}/>*/}
        {/*<DefinedInput placeholder="ZIP Code" prefix="zip"*/}
        {/*isChecked={this.props.fields.values.zip.checked}/>*/}
        {/*</Form.Items>*/}
        {/*</Col>*/}
        {/*</Row>*/}
        {/*</FormItem>*/}


        <FormItem offset={false} visible={!this.props.fields.isDefaultsEnabled}>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Items offset="small">
                <OptionDefault.Static placeholder="First name" prefix="firstName"
                                      value={this.props.fields.isFirstNameEnabled}/>
                <OptionDefault.Static placeholder="Last name" prefix="lastName"
                                      value={this.props.fields.isLastNameEnabled}/>
              </Form.Items>
            </Col>
            <Col span={12}>
              <Form.Items offset="small">
                <OptionDefault.Static placeholder="E-mail address" prefix="email"
                                      value={this.props.fields.isEmailEnabled}/>
                <OptionDefault.Static placeholder="Phone number" prefix="phone"
                                      value={this.props.fields.isPhoneEnabled}/>
                <OptionDefault.Static placeholder="Street address" prefix="streetAddress"
                                      value={this.props.fields.isStreetAddressEnabled}/>
                <OptionDefault.Static placeholder="City" prefix="city" value={this.props.fields.isCityEnabled}/>
                <OptionDefault.Static placeholder="State" prefix="state" value={this.props.fields.isStateEnabled}/>
                <OptionDefault.Static placeholder="ZIP Code" prefix="zip" value={this.props.fields.isZipEnabled}/>
              </Form.Items>
            </Col>
          </Row>
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

export default AddressField;
