import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import BaseField from '../BaseField';
import {Unit} from 'services/Products';
import FieldStub from 'scenes/Products/components/FieldStub';

export default class UnitStaticField extends BaseField.Static {

  Unit = {
    'Length, Distance': {
      'Imperial': [
        Unit.Inch,
        Unit.Foot,
        Unit.Yard,
        Unit.Mile,
        Unit.SquareFeet,
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

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Measurement</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Units</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>

            <FieldStub style={{width: '50%'}}>
              {this.props.name}
            </FieldStub>
            <FieldStub style={{width: '25%'}}>
              {this.props.units}
            </FieldStub>
            <FieldStub style={{width: '25%'}}>
              {this.props.value}
            </FieldStub>

          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}
