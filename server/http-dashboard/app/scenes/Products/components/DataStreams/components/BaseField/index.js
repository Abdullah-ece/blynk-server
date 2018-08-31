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
import Static from './static';
// import _ from 'lodash';
import {Map} from 'immutable';
import PropTypes from 'prop-types';

class BaseField extends React.Component {

  static propTypes = {
    onClone: PropTypes.func,
    onDelete: PropTypes.func,
    onChange: PropTypes.func,

    field: PropTypes.instanceOf(Map),
    fieldSyncErrors: PropTypes.oneOfType([PropTypes.instanceOf(Map), PropTypes.object]),
    name: PropTypes.string,
    isDirty: PropTypes.bool,
  };

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
    this.handleClone = this.handleClone.bind(this);
  }

  state = {
    isFocused: false
  };

  // shouldComponentUpdate(nextProps, nextState) {
  //   return this.state.isFocused !== nextState.isFocused || !(_.isEqual(this.props.field, nextProps.field)) || !(_.isEqual(this.props.fields, nextProps.fields)) || !(_.isEqual(this.state, nextState));
  // }

  onFocus() {
    this.setState({
      isFocused: true
    });
  }

  onBlur() {
    this.setState({
      isFocused: false
    });
  }

  Unit = {
    'Default' : [
      Unit.None
    ],
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
    ],
    'Other': [
      Unit.Percentage,
      Unit.RPM,
    ]
  };

  handleDelete() {
    this.props.onDelete(this.props.field.get('id'));
  }

  handleClone() {
    this.props.onClone(this.props.field.get('id'));
  }

  getPreviewValues() {
    const name = this.props.field.get('label');
    const min = this.props.field.get('min');
    const max = this.props.field.get('max');
    const units = this.props.field.get('units');

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
            <MetadataFormField onFocus={this.onFocus} onBlur={this.onBlur} validateOnBlur={true}
                               name={`${this.props.name}.label`} type="text" placeholder="Field Name"
                               style={{width: '40%'}} className={`datastream-name-field-${this.props.field.get('id')}`}
                               validate={[
              Validation.Rules.metafieldName,
              Validation.Rules.required
            ]}/>
            <MetadataFormSelect style={{width: '20%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                name={`${this.props.name}.units`} type="text" placeholder="Choose"
                                dropdownClassName="product-metadata-item-unit-dropdown"
                                values={this.Unit}
                                validate={[Validation.Rules.required]}/>

            <MetadataFormField style={{width: '20%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                               name={`${this.props.name}.min`} type="text" placeholder="Min" validate={[
              Validation.Rules.number
            ]}/>
            <MetadataFormField style={{width: '20%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                               name={`${this.props.name}.max`} type="text" placeholder="Max" validate={[
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
        onChange={this.props.onChange}
        onDelete={this.handleDelete}
        onClone={this.handleClone}
        field={this.props.field}
        isDirty={this.props.isDirty}
        fieldSyncErrors={this.props.fieldSyncErrors}
        name={this.props.name}
        isActive={this.state.isFocused}
      >
        { this.component() }
      </DataStreamsItem>
    );
  }
}

BaseField.Static = Static;

export default BaseField;
