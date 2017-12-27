import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField, MetadataSelect as MetadataFormSelect} from 'components/Form';
import {formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import {Unit} from 'services/Products';
import Static from './static';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      value: selector(state, 'value'),
      units: selector(state, 'units')
    }
  };
})
class UnitField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);

  }

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
    const name = this.props.fields.name;
    const value = this.props.fields.value;
    const units = this.props.fields.units;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' && units ? `${value} ${Unit[units].abbreviation}` : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Measurement</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Units</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField className={`metadata-name-field-${this.props.field.id}`}
                               onFocus={this.onFocus} onBlur={this.onBlur}
                               validateOnBlur={true} name="name" type="text" placeholder="Field Name"
                               style={{width: '200%'}} validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
            <MetadataFormSelect onFocus={this.onFocus} onBlur={this.onBlur}
                                name="units" type="text" placeholder="Choose"
                                dropdownClassName="product-metadata-item-unit-dropdown" values={this.Unit}/>
            <MetadataFormField onFocus={this.onFocus} onBlur={this.onBlur}
                               name="value" type="text" placeholder="Default val..." validate={[
              Validation.Rules.number
            ]}/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

UnitField.Static = Static;

export default UnitField;
