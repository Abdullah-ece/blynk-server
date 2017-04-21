import React from 'react';
import Metadata from '../../index';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField, MetadataSelect as MetadataFormSelect} from 'components/Form';
import {reduxForm, formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      value: selector(state, 'value'),
      measure: selector(state, 'measure')
    }
  };
})
@reduxForm({
  touchOnChange: true
})
export default class UnitField extends React.Component {

  static propTypes = {
    id: React.PropTypes.number,
    fields: React.PropTypes.object,
    pristine: React.PropTypes.bool,
    invalid: React.PropTypes.bool,
    anyTouched: React.PropTypes.bool,
    onDelete: React.PropTypes.func,
    onClone: React.PropTypes.func,
    isUnique: React.PropTypes.func
  };

  Unit = {
    'Length, Distance': {
      'Imperial': [
        {
          key: 'in',
          value: 'Inch'
        },
        {
          key: 'ft',
          value: 'Foot'
        },
        {
          key: 'yd',
          value: 'Yard'
        },
        {
          key: 'mi',
          value: 'Mile'
        }
      ],
      'Metric': [
        {
          key: 'mm',
          value: 'Millimeter'
        },
        {
          key: 'cm',
          value: 'Centimeter'
        },
        {
          key: 'm',
          value: 'Meter'
        },
        {
          key: 'km',
          value: 'Kilometer'
        },
      ]
    },
    'Mass': {
      'Imperial': [
        {
          key: 'oz',
          value: 'Ounce'
        },
        {
          key: 'lb',
          value: 'Pound'
        },
        {
          key: 'st',
          value: 'Stone'
        },
        {
          key: 'qrt',
          value: 'Quarter'
        },
        {
          key: 'cwt',
          value: 'Hundredweight'
        },
        {
          key: 't',
          value: 'Ton'
        }
      ],
      'Metric': [
        {
          key: 'mg',
          value: 'Milligram'
        },
        {
          key: 'g',
          value: 'Gram'
        },
        {
          key: 'kg',
          value: 'Kilogram'
        },
        {
          key: 't',
          value: 'Tonne'
        },
      ]
    },
    'Volume': {
      'Imperial': [
        {
          key: 'pt',
          value: 'Pint'
        },
        {
          key: 'gal',
          value: 'Gallon'
        }
      ],
      'Metric': [
        {
          key: 'lt',
          value: 'Liter'
        }
      ]
    },
    'Temperature': [
      {
        key: '°C',
        value: 'Celsius'
      },
      {
        key: '°F',
        value: 'Fahrenheit'
      },
      {
        key: '°K',
        value: 'Kelvin'
      }
    ]
  };

  getPreviewValues() {
    const name = this.props.fields.name;
    const value = this.props.fields.value;
    const measure = this.props.fields.measure;

    return {
      values: {
        name: name && typeof name === 'string' ? `${name.trim()}:` : null,
        value: value && typeof value === 'string' && measure ? `${value} ${measure}` : null
      },
      isTouched: this.props.anyTouched,
      invalid: this.props.invalid
    };
  }

  handleDelete() {
    if (this.props.onDelete)
      this.props.onDelete(this.props.id);
  }

  handleClone() {
    if (this.props.onClone)
      this.props.onClone(this.props.id);
  }

  render() {

    return (
      <Metadata.Item touched={this.props.anyTouched} preview={this.getPreviewValues()}
                     onDelete={this.handleDelete.bind(this)}
                     onClone={this.handleClone.bind(this)}>
        <FormItem offset={false}>
          <FormItem.TitleGroup>
            <FormItem.Title style={{width: '50%'}}>Measurement</FormItem.Title>
            <FormItem.Title style={{width: '25%'}}>Units</FormItem.Title>
            <FormItem.Title style={{width: '25%'}}>Value</FormItem.Title>
          </FormItem.TitleGroup>
          <FormItem.Content>
            <Input.Group compact>
              <MetadataFormField name="name" type="text" placeholder="Field Name" style={{width: '200%'}} validate={[
                Validation.Rules.required
              ]}/>
              <MetadataFormSelect name="measure" type="text" placeholder="Choose"
                                  dropdownClassName="product-metadata-item-unit-dropdown" values={this.Unit}/>
              <MetadataFormField name="value" type="text" placeholder="Default val..."/>
            </Input.Group>
          </FormItem.Content>
        </FormItem>
      </Metadata.Item>
    );
  }
}
