import React from 'react';
import DataStreamsItem from '../Item';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {
  MetadataField as MetadataFormField,
  MetadataSelect as MetadataFormSelect
} from 'components/Form';
import Validation from 'services/Validation';
import {Unit} from 'services/Products';
import {connect} from 'react-redux';
import {formValueSelector} from 'redux-form';
import Static from './static';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      units: selector(state, 'units'),
      min: selector(state, 'min'),
      max: selector(state, 'max'),
      pin: selector(state, 'pin')
    }
  };
})
class BaseField extends React.Component {

  static propTypes = {
    onClone: React.PropTypes.func,
    onDelete: React.PropTypes.func,
    onChange: React.PropTypes.func,
    validate: React.PropTypes.func,

    initialValues: React.PropTypes.object,

    id: React.PropTypes.number,

    form: React.PropTypes.string,

    fields: React.PropTypes.object,
    field: React.PropTypes.object,
  };

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

  handleDelete() {
    this.props.onDelete(this.props.id);
  }

  handleClone() {
    this.props.onClone(this.props.id);
  }

  getPreviewValues() {
    const name = this.props.fields.name;
    const min = this.props.fields.min;
    const max = this.props.fields.max;
    const units = this.props.fields.units;

    let value = null;
    if (!isNaN(Number(min)) && !isNaN(Number(max)) && units) {
      value = `from ${min} to ${max} ${Unit[units].abbreviation}`;
    }

    return {
      name: name && typeof name === 'string' ? `${name.trim()}:` : null,
      value: value
    };
  }

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
            <MetadataFormField name="name" type="text" placeholder="Field Name" style={{width: '200%'}} validate={[
              Validation.Rules.metafieldName,
              Validation.Rules.required
            ]}/>
            <MetadataFormSelect name="units" type="text" placeholder="Choose"
                                dropdownClassName="product-metadata-item-unit-dropdown"
                                values={this.Unit}
                                validate={[Validation.Rules.required]}/>

            <MetadataFormField name="min" type="text" placeholder="Min" validate={[
              Validation.Rules.number
            ]}/>
            <MetadataFormField name="max" type="text" placeholder="Max" validate={[
              Validation.Rules.number
            ]}/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }

  render() {
    return (
      <DataStreamsItem
        preview={this.getPreviewValues()}
        onChange={this.props.onChange.bind(this)}
        onDelete={this.handleDelete.bind(this)}
        onClone={this.handleClone.bind(this)}
        validate={this.props.validate.bind(this)}
        initialValues={this.props.initialValues}
        fields={this.props.fields}
        field={this.props.field}
        id={this.props.id}
        form={this.props.form}
      >
        { this.component() }
      </DataStreamsItem>
    );
  }
}

BaseField.Static = Static;

export default BaseField;
