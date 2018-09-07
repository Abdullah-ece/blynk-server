import React from 'react';
import FormItem from 'components/FormItem';
import {Col, Row, Switch, Checkbox, Input} from 'antd';
import _ from 'lodash';
import {MetadataField as MetadataFormField} from 'components/Form';
import Validation from 'services/Validation';
import {LocationAutocomplete} from "components";
import {FORMS} from "services/Products";
import BaseField from '../BaseField/index';
import {Form} from 'components/UI';
import Static from './static';
import {Field, Fields, change} from 'redux-form';

import './styles.less';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
@connect(() => ({}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch)
}))
class LocationField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);
    this.fieldLabeledCheckbox = this.fieldLabeledCheckbox.bind(this);
    this.handleAddressSelect = this.handleAddressSelect.bind(this);
  }

  getPreviewValues() {
    const name = this.props.field.get('name');
    let value = [];

    const fields = [
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
      {
        name: 'country',
        check: 'isCountryEnabled'
      },
      {
        name: 'buildingName',
        check: 'isBuildingNameEnabled'
      },
      {
        name: 'floor',
        check: 'isFloorEnabled'
      },
      {
        name: 'unit',
        check: 'isUnitEnabled'
      },
      {
        name: 'room',
        check: 'isRoomEnabled'
      },
      {
        name: 'zone',
        check: 'isZoneEnabled'
      },

    ];

    const placeholders = {
      streetAddress: 'Street Address',
      city: 'City',
      state: 'State',
      country: 'Country',
      buildingName: 'Building Name',
      floor: 'Floor',
      unit: 'Unit',
      room: 'Room',
      zone: 'Zone',
      zip: 'ZIP',
    };

    const checkIsFieldValid = (field) => {
      return !!this.props.field.get(field.check);
    };

    (fields).forEach((field) => {
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
      <Switch size="small" className="location-field-allow-default-values-switch" checked={!!props.input.value}
              onChange={(value) => {
                props.input.onChange(value);
              }}/>
    );
  }

  handleAddressSelect(values) {

    const form = FORMS.PRODUCTS_PRODUCT_MANAGE;
    const field = (fieldName) => `metaFields.${this.props.metaFieldKey}.${fieldName}`;

    let streetString = '';

    if(values.street)
      streetString += values.street;

    if(values.number)
      streetString += ` ${values.number}`;

    if(streetString) {
      this.props.changeForm(form, field(`street`), streetString);
    }

    if(values.city) {
      this.props.changeForm(form, field(`city`), values.city);
    }

    if(values.state) {
      this.props.changeForm(form, field(`state`), values.state);
    }

    if(values.country) {
      this.props.changeForm(form, field(`country`), values.country);
    }

    if(values.postal) {
      this.props.changeForm(form, field(`zip`), values.postal);
    }

  }

  labeledCheckbox(props) {
    return (
      <div className={`location-field-values-list-item`}>
        <Checkbox className={`location-field-values-list-item-checkbox`}
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
      <div className={`location-field-values-list-item`}>

        <Form.Items layout="inline">
          <Form.Item className={`location-field-values-list-item-checkbox`}>
            <Checkbox size="small"
                      checked={!!checkbox.input.value}
                      onChange={checkbox.input.onChange}
            />
          </Form.Item>
          <Form.Item className={`location-field-values-list-item-field`} style={{width: '100%', maxWidth: '185px'}}>
            { props.isLocationAutocomplete ? (
              <LocationAutocomplete style={{width: '100%', maxWidth: '185px'}} onChange={field.input.onChange}
                                    onSelect={this.handleAddressSelect}
                                    value={field.input.value}
                                    onFocus={this.onFocus}
                                    onBlur={this.onBlur}
                                    placeholder={props.label}
                                    disabled={!checkbox.input.value}
              />
            ) : (
              <Input onBlur={this.onBlur}
                     onFocus={this.onFocus}
                     value={field.input.value}
                     onChange={field.input.onChange}
                     placeholder={props.label}
                     disabled={!checkbox.input.value}/>
            )}
          </Form.Item>
        </Form.Items>

      </div>
    );
  }

  addBefore() {
    return(
      <Form.Item offset="small" checkbox={true}>
        <FormItem.Title>Location</FormItem.Title>
        <FormItem.Content>
          <Row className={`location-field-enable-location`}>
            <Col span={4}>
              <Field name={`metaFields.${this.props.metaFieldKey}.isLocationEnabled`}
                     component={this.switch}/>
              <span className="location-field-enable-location-values-title"> Enable Location</span>
            </Col>
            <Col span={20}>
              <div style={{display: (this.props.field.get('isLocationEnabled') && 'inline-block' || 'none')}}>
                <Field name={`metaFields.${this.props.metaFieldKey}.isLocationGetFromDevice`}
                       component={this.switch}/>
                <span className="location-field-enable-location-values-title"> Use location data from device (e.g. GPS)</span>
              </div>
            </Col>
            {this.props.field.get('isLocationEnabled') && this.props.field.get('isLocationGetFromDevice') &&(
              <Col span={24} className="location-field-location-from-device-info">
                <span>Set up location Datastream in Datastreams section</span>
              </Col>
            )}
          </Row>
        </FormItem.Content>
      </Form.Item>
    );
  }

  component() {

    const getColumns = (renderFieldsWithInputs) => ([
      [
        {
          label: `Street address`,
          isLocationAutocomplete: true,
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
          label: `State/Province`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isStateEnabled`,
            `metaFields.${this.props.metaFieldKey}.state`
          ]
        },
        {
          label: `ZIP/Postal Code`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isZipEnabled`,
            `metaFields.${this.props.metaFieldKey}.zip`
          ]
        },
        {
          label: `Country`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isCountryEnabled`,
            `metaFields.${this.props.metaFieldKey}.country`
          ]
        },
        {
          label: `Coordinates`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isCoordinatesEnabled`,
            `metaFields.${this.props.metaFieldKey}.lat`,
            `metaFields.${this.props.metaFieldKey}.lon`
          ]
        }
      ],
      [
        {
          label: `Building Name`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isBuildingNameEnabled`,
            `metaFields.${this.props.metaFieldKey}.buildingName`
          ]
        },
        {
          label: `Floor`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isFloorEnabled`,
            `metaFields.${this.props.metaFieldKey}.floor`
          ]
        },
        {
          label: `Unit`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isUnitEnabled`,
            `metaFields.${this.props.metaFieldKey}.unit`
          ]
        },
        {
          label: `Room`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isRoomEnabled`,
            `metaFields.${this.props.metaFieldKey}.room`
          ]
        },
        {
          label: `Zone`,
          names: [
            `metaFields.${this.props.metaFieldKey}.isZoneEnabled`,
            `metaFields.${this.props.metaFieldKey}.zone`
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

    const valuesListClassName = this.props.field.get('isDefaultsEnabled') ?
      'location-field-values-list-defaults' : 'location-field-values-list-non-defaults';

    return (
      <div>
        <div>
          <FormItem offset={false}>
            <FormItem.Title>Location</FormItem.Title>
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
            <div className={`location-field-allow-default-values`}>
              <Field name={`metaFields.${this.props.metaFieldKey}.isDefaultsEnabled`}
                     component={this.switch}/>
              <span className="location-field-allow-default-values-title"> Allow default values</span>
            </div>
          </Form.Item>

          <FormItem offset={false}>
            <Row gutter={8}>
              <Col span={12}>
                <div className="location-field--column-title">
                  ADDRESS ELEMENTS
                </div>
              </Col>
              <Col span={12}>
                <div className="location-field--column-title">
                  PREMISES ELEMENTS
                </div>
              </Col>
            </Row>
            <Row gutter={8}>
              {getColumns(!!this.props.field.get('isDefaultsEnabled')).map((column, i)=>{
                return (
                  <Col span={12} key={i}>
                    <Form.Items className={`location-field-values-list ${valuesListClassName}`}>
                        {column}
                    </Form.Items>
                  </Col>
                );
              })}
            </Row>
          </FormItem>
        </div>

      </div>
    );
  }

}

LocationField.Static = Static;
export default LocationField;
