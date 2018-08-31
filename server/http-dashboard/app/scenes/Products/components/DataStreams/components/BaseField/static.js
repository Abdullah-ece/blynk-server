import React from 'react';
import DataStreamsItem from '../Item';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {Unit} from 'services/Products';
import Static from './static';

class BaseField extends React.Component {

  static propTypes = {
    id: React.PropTypes.number,
    min: React.PropTypes.number,
    max: React.PropTypes.number,
    pin: React.PropTypes.number,

    form: React.PropTypes.string,
    label: React.PropTypes.string,
    units: React.PropTypes.string,

    fields: React.PropTypes.object,
    field: React.PropTypes.object,
  };

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
        Unit.Gallon,
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
    const name = this.props.label;
    const min = this.props.min;
    const max = this.props.max;
    const units = this.props.units;

    let value = null;

    if (units) {
      value = `0 ${Unit[units].abbreviation}`;
    }

    if (!isNaN(Number(max)) && units) {
      value = `${max} ${Unit[units].abbreviation}`;
    }

    if (!isNaN(Number(min)) && units) {
      value = `${min} ${Unit[units].abbreviation}`;
    }

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value
    };
  }

  DEFAULT_VALUE = 'No Value';

  component() {
    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Name</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Units</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Min</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Max</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <div className="product-metadata-static-field" style={{width: '40%'}}>
              {this.props.label}
            </div>
            <div className="product-metadata-static-field" style={{width: '20%'}}>
              {this.props.units || '--'}
            </div>
            <div className="product-metadata-static-field" style={{width: '20%'}}>
              {this.props.min !== undefined ? this.props.min : '--'}
            </div>
            <div className="product-metadata-static-field" style={{width: '20%'}}>
              { this.props.max !== undefined ? this.props.max : '--' }
            </div>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }

  render() {
    return (
      <DataStreamsItem.Static
        {...this.props}
        preview={this.getPreviewValues()}
        id={this.props.id}
      >
        { this.component() }
      </DataStreamsItem.Static>
    );
  }
}

BaseField.Static = Static;

export default BaseField;
