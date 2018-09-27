import React from 'react';
import FormItem from 'components/FormItem';
import {Input, Col, Row} from 'antd';
import {
  MetadataField as MetadataFormField,
  MetadataSelect as MetadataFormSelect,
  Number as MetadataFormNumber,
  Field,
} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import {Currency, Unit} from 'services/Products';
import Static from './static';

class CostField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);

  }

  Currency = [
    Currency.USD,
    Currency.EUR,
    Currency.GBP,
    Currency.CNY,
    Currency.RUB
  ];

  Unit = {
    'Length, Distance': {
      'Imperial': [
        Unit.Inch,
        Unit.Foot,
        Unit.Yard,
        Unit.Mile
      ],
      'Metric': [
        Unit.Millimeter,
        Unit.Centimeter,
        Unit.Meter,
        Unit.Kilometer
      ]
    },
    'Mass': {
      'Imperial': [
        Unit.Ounce,
        Unit.Pound,
        Unit.Stone,
        Unit.Quarter,
        Unit.Hundredweight,
        Unit.Ton,
        Unit.Tonne,
      ],
      'Metric': [
        Unit.Milligram,
        Unit.Gram,
        Unit.Kilogram
      ]
    },
    'Volume': {
      'Imperial': [
        Unit.Pint,
        Unit.Gallon
      ],
      'Metric': [
        Unit.Liter
      ]
    },
    'Temperature': [
      Unit.Celsius,
      Unit.Fahrenheit,
      Unit.Kelvin
    ],
    'Other': [
      Unit.Percentage,
    ]
  };

  getPreviewValues() {
    const fieldName = this.props.field.get('name');
    const perValue = this.props.field.get('perValue');
    const price = this.props.field.get('price');
    const units = this.props.field.get('units');
    const currency = this.props.field.get('currency');

    let name = null;
    let value = null;

    if (fieldName) {
      name = fieldName && typeof fieldName === 'string' ? `${fieldName.trim()}` : null;
    }

    if (price) {
      value = currency ? `${Currency[currency].abbreviation} ${price}` : null;
    }
    if (fieldName && price && perValue && units) {
      if (Number(perValue) === 1) {
        value = `${Currency[currency].abbreviation} ${price} / ${Unit[units].abbreviation}`;
      } else {
        value = `${Currency[currency].abbreviation} ${price} / ${perValue} ${Unit[units].abbreviation}`;
      }
    }

    return {
      name: name,
      value: value
    };
  }

  component() {

    return (
      <div>

        <Field title="Cost name"
               className={`metadata-name-field-${this.props.field.get('id')} normal-offset`}
               onFocus={this.onFocus}
               onBlur={this.onBlur}
               validateOnBlur={true}
               name={`metaFields.${this.props.metaFieldKey}.name`}
               placeholder="Name"
               validate={[Validation.Rules.required]}
        />

        <Row>
          <Col span={7}>
            <FormItem offset={false}>
              <FormItem.TitleGroup>
                <FormItem.Title style={{width: '50%'}}>Currency</FormItem.Title>
                <FormItem.Title style={{width: '50%'}}>Price</FormItem.Title>
              </FormItem.TitleGroup>
              <FormItem.Content>
                <Input.Group compact>

                  <MetadataFormSelect style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                      name={`metaFields.${this.props.metaFieldKey}.currency`} type="text" placeholder="Choose" values={this.Currency}/>

                  <MetadataFormNumber style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                      name={`metaFields.${this.props.metaFieldKey}.price`} type="text" placeholder="--" validate={[
                    Validation.Rules.number
                  ]}/>

                </Input.Group>
              </FormItem.Content>
            </FormItem>

          </Col>
          <Col span={8} offset={1}>
            <FormItem offset={false}>
              <FormItem.TitleGroup>
                <FormItem.Title style={{width: '50%'}}>PER QUANTITY</FormItem.Title>
                <FormItem.Title style={{width: '50%'}}>UNITS</FormItem.Title>
              </FormItem.TitleGroup>
              <FormItem.Content>
                <Input.Group compact>
                  <MetadataFormNumber style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                      name={`metaFields.${this.props.metaFieldKey}.perValue`} type="text" placeholder="--" validate={[
                    Validation.Rules.number
                  ]}/>
                  <MetadataFormSelect style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                      name={`metaFields.${this.props.metaFieldKey}.units`} type="text" placeholder="--"
                                      dropdownClassName="product-metadata-item-unit-dropdown"
                                      values={this.Unit}/>
                </Input.Group>
              </FormItem.Content>
            </FormItem>

          </Col>
          <Col span={7} offset={1}>
            <FormItem offset={false}>
              <FormItem.TitleGroup>
                <FormItem.Title style={{width: '100%'}}>Min/Max values (optional)</FormItem.Title>
              </FormItem.TitleGroup>
              <FormItem.Content>
                <Input.Group compact>

                  <MetadataFormField style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                     name={`metaFields.${this.props.metaFieldKey}.min`} type="text" placeholder="Min" validate={[
                    Validation.Rules.number
                  ]}/>
                  <MetadataFormField style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                     name={`metaFields.${this.props.metaFieldKey}.max`} type="text" placeholder="Max" validate={[
                    Validation.Rules.number
                  ]}/>

                </Input.Group>
              </FormItem.Content>
            </FormItem>

          </Col>
        </Row>

      </div>

    );
  }
}

CostField.Static = Static;

export default CostField;
