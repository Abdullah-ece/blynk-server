import React from 'react';
import FormItem from 'components/FormItem';
import {Col, Row, Switch} from 'antd';
import {MetadataField as MetadataFormField} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import {Form} from 'components/UI';
import Static from './static';
import {Field} from 'redux-form';
import './styles.less';

class ContactField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);

  }

  getPreviewValues() {

    const name = this.props.field.get('name');
    const value = this.props.field.get('value');

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
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
        <FormItem offset={false}>
          <FormItem.Title>Contact</FormItem.Title>
          <FormItem.Content>
            <MetadataFormField className={`metadata-name-field-${this.props.field.get('id')}`}
                               onFocus={this.onFocus} onBlur={this.onBlur}
                               validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text"
                               placeholder="Field Name" validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
          </FormItem.Content>
        </FormItem>

        <Form.Item offset="small" checkbox={true}>
          <div className={`contact-field-allow-default-values`}>
            <Field name={`metaFields.${this.props.metaFieldKey}.isDefaultsEnabled`}
                   component={this.switch}/>
            <span className="contact-field-allow-default-values-title"> Allow default values</span>
          </div>
        </Form.Item>

        <FormItem offset={false}>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Items offset="small">
                {/*<div>*/}

                {/*</div>*/}
                {/*<DefinedInput placeholder="First name" prefix="firstName"*/}
                              {/*isChecked={this.props.fields.values.firstName.checked}*/}
                              {/*onFocus={this.onFocus} onBlur={this.onBlur}*/}
                              {/*value={this.props.fields.values.firstName.value}/>*/}
                {/*<DefinedInput placeholder="Last name" prefix="lastName"*/}
                              {/*isChecked={this.props.fields.values.lastName.checked}*/}
                              {/*onFocus={this.onFocus} onBlur={this.onBlur}*/}
                              {/*value={this.props.fields.values.lastName.value}/>*/}
              </Form.Items>
            </Col>
            <Col span={12}>
              {/*<Form.Items offset="small">*/}
                {/*<DefinedInput placeholder="E-mail address" prefix="email"*/}
                              {/*onFocus={this.onFocus} onBlur={this.onBlur}*/}
                              {/*isChecked={this.props.fields.values.email.checked}*/}
                              {/*value={this.props.fields.values.email.value}*/}
                  {/*// popconfirm={popconfirmOptions}*/}
                {/*/>*/}
                {/*<DefinedInput placeholder="Phone number" prefix="phone"*/}
                              {/*onFocus={this.onFocus} onBlur={this.onBlur}*/}
                              {/*isChecked={this.props.fields.values.phone.checked}*/}
                              {/*value={this.props.fields.values.phone.value}/>*/}
                {/*<DefinedInput placeholder="Street address" prefix="streetAddress"*/}
                              {/*onFocus={this.onFocus} onBlur={this.onBlur}*/}
                              {/*isChecked={this.props.fields.values.streetAddress.checked}*/}
                              {/*value={this.props.fields.values.streetAddress.value}/>*/}
                {/*<DefinedInput placeholder="City" prefix="city"*/}
                              {/*onFocus={this.onFocus} onBlur={this.onBlur}*/}
                              {/*isChecked={this.props.fields.values.city.checked}*/}
                              {/*value={this.props.fields.values.city.value}/>*/}
                {/*<DefinedInput placeholder="State" prefix="state"*/}
                              {/*onFocus={this.onFocus} onBlur={this.onBlur}*/}
                              {/*isChecked={this.props.fields.values.state.checked}*/}
                              {/*value={this.props.fields.values.state.value}/>*/}
                {/*<DefinedInput placeholder="ZIP Code" prefix="zip"*/}
                              {/*onFocus={this.onFocus} onBlur={this.onBlur}*/}
                              {/*isChecked={this.props.fields.values.zip.checked}*/}
                              {/*value={this.props.fields.values.zip.value}/>*/}
              {/*</Form.Items>*/}
            </Col>
          </Row>
        </FormItem>
      </div>
    );
  }

}

ContactField.Static = Static;
export default ContactField;
