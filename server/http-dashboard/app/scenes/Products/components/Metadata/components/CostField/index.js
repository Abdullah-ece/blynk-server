import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {
  MetadataField as MetadataFormField,
  MetadataSelect as MetadataFormSelect,
  Number as MetadataFormNumber
} from 'components/Form';
import {formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import {Currency, Unit} from 'services/Products';
import Static from './static';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      price: selector(state, 'price'),
      currency: selector(state, 'currency'),
      perValue: selector(state, 'perValue'),
      units: selector(state, 'units')
    }
  };
})
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
      Unit.Percentage
    ]
  };

  getPreviewValues() {
    const fieldName = this.props.fields.name;
    const perValue = this.props.fields.perValue;
    const price = this.props.fields.price;
    const units = this.props.fields.units;
    const currency = this.props.fields.currency;

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
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Cost of</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>CCY</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Price</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Per</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Unit</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField className={`metadata-name-field-${this.props.field.id}`}
                               validateOnBlur={true} name="name" type="text" placeholder="Field Name"
                               onFocus={this.onFocus} onBlur={this.onBlur}
                               style={{width: '200%'}} validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
            <MetadataFormSelect onFocus={this.onFocus} onBlur={this.onBlur}
                                name="currency" type="text" placeholder="Choose" values={this.Currency}/>

            <MetadataFormNumber onFocus={this.onFocus} onBlur={this.onBlur}
                                name="price" type="text" placeholder="--" validate={[
              Validation.Rules.number
            ]}/>
            <MetadataFormNumber onFocus={this.onFocus} onBlur={this.onBlur}
                                name="perValue" type="text" placeholder="--" validate={[
              Validation.Rules.number
            ]}/>
            <MetadataFormSelect onFocus={this.onFocus} onBlur={this.onBlur}
                                name="units" type="text" placeholder="--"
                                dropdownClassName="product-metadata-item-unit-dropdown"
                                values={this.Unit}/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

CostField.Static = Static;

export default CostField;
