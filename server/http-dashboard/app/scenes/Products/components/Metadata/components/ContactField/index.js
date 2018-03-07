import React from 'react';
import FormItem from 'components/FormItem';
import {Col, Row, Switch, Checkbox, Input} from 'antd';
import _ from 'lodash';
import {MetadataField as MetadataFormField} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import {Form} from 'components/UI';
import Static from './static';
import {Field, Fields} from 'redux-form';
import './styles.less';

class ContactField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);
    this.fieldLabeledCheckbox = this.fieldLabeledCheckbox.bind(this);

  }

  getPreviewValues() {
    const name = this.props.field.get('name');
    let value = [];

    const fields = [
      {
        name: 'firstName',
        check: 'isFirstNameEnabled'
      },
      {
        name: 'lastName',
        check: 'isLastNameEnabled'
      },
      {
        name: 'email',
        check: 'isEmailEnabled'
      },
      {
        name: 'phone',
        check: 'isPhoneEnabled'
      },
      {
        name: 'streetAddress',
        check: 'isStreetAddressEnabled'
      },
      {
        name: 'city',
        check: 'isCityEnabled'
      },
      {
        name: 'state',
        check: 'isStateEnabled'
      },
      {
        name: 'zip',
        check: 'isZipEnabled'
      },

    ];

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

    const checkIsFieldValid = (field) => {
      return !!this.props.field.get(field.check);
    };

    if( fields.slice(0, 2).every(checkIsFieldValid) ) {
      let firstName, lastName;
      if(this.props.field.get('isDefaultsEnabled')) {
        firstName = this.props.field.get(fields[0].name) || placeholders.firstName;
        lastName = this.props.field.get(fields[1].name) || placeholders.lastName;
      } else {
        firstName = placeholders.firstName;
        lastName = placeholders.lastName;
      }
      value.push(`${firstName}, ${lastName}`);
    }

    (value.length ? fields.slice(2) : fields).forEach((field) => {
      if (checkIsFieldValid(field)) {
        if (this.props.field.get('isDefaultsEnabled') && this.props.field.get(field.name)) {
          value.push(
            this.props.field.get(field.name)
          );
        } else {
          value.push(placeholders[field.name]);
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

  labeledCheckbox(props) {
    return (
      <div className={`contact-field-values-list-item`}>
        <Checkbox className={`contact-field-values-list-item-checkbox`}
                  size="small"
                  checked={!!props.input.value}
                  onChange={(value) => {
                    props.input.onChange(value);
                  }}>
          <span>{props.label}</span>
        </Checkbox>
      </div>
    );
  }
  fieldLabeledCheckbox(props) {


    // the path to element looks like props[`metaFields`][this.props.metaFieldKey][`isFirstNameEnabled`]
    // the name on props.names looks like `metaFields.6.isFirstNameEnabled`
    // so to take checkbox/field value/onChange handler we should take the last string after the last dot - isFirstNameEnabled

    const checkbox = _.get(props, props.names[0]);
    const field = _.get(props, props.names[1]);

    return (
      <div className={`contact-field-values-list-item`}>

        <Form.Items layout="inline">
          <Form.Item className={`contact-field-values-list-item-checkbox`}>
            <Checkbox size="small"
                      checked={!!checkbox.input.value}
                      onChange={checkbox.input.onChange}
            />
          </Form.Item>
          <Form.Item className={`contact-field-values-list-item-field`}>
            <Input onBlur={this.onBlur}
                   onFocus={this.onFocus}
                   value={field.input.value}
                   onChange={field.input.onChange}
                   placeholder={props.label}
                   disabled={!checkbox.input.value}/>
          </Form.Item>
        </Form.Items>

      </div>
    );
  }

  component() {

    const getColumns = (renderFieldsWithInputs) => ([
      [
        {
          label: `First Name`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isFirstNameEnabled`,
            `metaFields.${this.props.metaFieldKey}.firstName`
          ]
        },
        {
          label: `Last Name`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isLastNameEnabled`,
            `metaFields.${this.props.metaFieldKey}.lastName`
          ]
        }
      ],
      [
        {
          label: `E-mail Address`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isEmailEnabled`,
            `metaFields.${this.props.metaFieldKey}.email`
          ]
        },
        {
          label: `Phone Number`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isPhoneEnabled`,
            `metaFields.${this.props.metaFieldKey}.phone`
          ]
        },
        {
          label: `Street Address`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isStreetAddressEnabled`,
            `metaFields.${this.props.metaFieldKey}.streetAddress`
          ]
        },
        {
          label: `City`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isCityEnabled`,
            `metaFields.${this.props.metaFieldKey}.city`
          ]
        },
        {
          label: `State`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isStateEnabled`,
            `metaFields.${this.props.metaFieldKey}.state`
          ]
        },
        {
          label: `ZIP Code`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isZipEnabled`,
            `metaFields.${this.props.metaFieldKey}.zip`
          ]
        },
      ]
    ].map((column) => {

      if (renderFieldsWithInputs) {

        return column.map((item, key) => (
          <Fields {...item} key={key}
                  component={this.fieldLabeledCheckbox}/>
        ));

      } else {

        return column.map((item, key) => (
          <Field key={key}
                 name={item.names[0]}
                 label={item.label}
                 onFocus={this.onFocus}
                 onBlur={this.onBlur}
                 component={this.labeledCheckbox}
          />
        ));

      }

    }));

    const valuesListClassName = this.props.field.get('isDefaultsEnabled') ? 'contact-field-values-list-defaults' : 'contact-field-values-list-non-defaults';

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
              <Form.Items className={`contact-field-values-list ${valuesListClassName}`}>

                <div style={{display: (this.props.field.get('isDefaultsEnabled') && 'none' || 'block')}}>
                  {getColumns(false)[0]}
                </div>

                <div style={{display: (this.props.field.get('isDefaultsEnabled') && 'block' || 'none')}}>
                  {getColumns(true)[0]}
                </div>

              </Form.Items>
            </Col>
            <Col span={12}>

              <Form.Items className={`contact-field-values-list ${valuesListClassName}`}>

                <div style={{display: (this.props.field.get('isDefaultsEnabled') && 'none' || 'block')}}>
                  {getColumns(false)[1]}
                </div>

                <div style={{display: (this.props.field.get('isDefaultsEnabled') && 'block' || 'none')}}>
                  {getColumns(true)[1]}
                </div>

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
