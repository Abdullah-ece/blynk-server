import React from 'react';
import FormItem from 'components/FormItem';
import {Col, Row, Switch, Checkbox} from 'antd';
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

  labeledCheckbox(props) {
    return (
      <div className={`contact-field-values-list-item`}>
        <Checkbox size="small" className="contact-field-allow-default-values-switch" checked={!!props.input.value}
                  onChange={(value) => {
                    props.input.onChange(value);
                  }}>
          <span>{props.label}</span>
        </Checkbox>
      </div>
    );
  }

  component() {

    const columns = [
      [
        {
          label: `First Name`,
          name: `metaFields.${this.props.metaFieldKey}.isFirstNameEnabled`
        },
        {
          label: `Last Name`,
          name: `metaFields.${this.props.metaFieldKey}.isLastNameEnabled`
        }
      ],
      [
        {
          label: `E-mail Address`,
          name: `metaFields.${this.props.metaFieldKey}.isEmailEnabled`
        },
        {
          label: `Phone Number`,
          name: `metaFields.${this.props.metaFieldKey}.isPhoneEnabled`
        },
        {
          label: `Street Address`,
          name: `metaFields.${this.props.metaFieldKey}.isStreetAddressEnabled`
        },
        {
          label: `City`,
          name: `metaFields.${this.props.metaFieldKey}.isCityEnabled`
        },
        {
          label: `State`,
          name: `metaFields.${this.props.metaFieldKey}.isStateEnabled`
        },
        {
          label: `ZIP Code`,
          name: `metaFields.${this.props.metaFieldKey}.isZipEnabled`
        },
      ]
    ].map((column) => column.map((item, key) => (
      <Field key={key}
             {...item}
             onFocus={this.onFocus}
             onBlur={this.onBlur}
             component={this.labeledCheckbox}
      />
    )));

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
              <Form.Items offset="small" className={`contact-field-values-list`}>

                { columns[0] }

              </Form.Items>
            </Col>
            <Col span={12}>

              <Form.Items offset="small" className={`contact-field-values-list`}>

                { columns[1] }

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
