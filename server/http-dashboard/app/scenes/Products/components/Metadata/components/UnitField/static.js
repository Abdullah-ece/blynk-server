import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import BaseField from '../BaseField';
import {Unit} from 'services/Products';
import classnames from 'classnames';

export default class UnitStaticField extends BaseField.Static {

  DEFAULT_VALUE = 'No Value';

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
        Unit.Ton
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
    ]
  };

  getPreviewValues() {
    const name = this.props.name;
    const value = this.props.value;
    const units = this.props.units;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' && units ? `${value} ${Unit[units].abbreviation}` : null
    };
  }

  component() {

    const valueClassNames = classnames({
      'product-metadata-static-field': true,
      'no-value': !this.props.value
    });

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Measurement</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Units</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>

            <div className="product-metadata-static-field" style={{width: '200%'}}>
              {this.props.name}
            </div>
            <div className="product-metadata-static-field">
              {this.props.units}
            </div>
            <div className={valueClassNames}>
              {this.props.value || this.DEFAULT_VALUE}
            </div>

          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}
