import React from 'react';
import FormItem from 'components/FormItem';
import {Input, Row, Col} from 'antd';
import {MetadataField as MetadataFormField, MetadataSelect as MetadataFormSelect, Field} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import {Unit} from 'services/Products';
import Static from './static';

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
        Unit.Mile,
        Unit.SquareFeet,
      ],
      'Metric'  : [
        Unit.Millimeter,
        Unit.Centimeter,
        Unit.Meter,
        Unit.Kilometer
      ]
    },
    'Duration'        : [
      Unit.Year,
      Unit.Month,
      Unit.Week,
      Unit.Day,
      Unit.Hour,
      Unit.Minute,
      Unit.Second
    ],
    'Mass'            : {
      'Imperial': [
        Unit.Ounce,
        Unit.Pound,
        Unit.Stone,
        Unit.Quarter,
        Unit.Hundredweight,
        Unit.Ton,
        Unit.Tonne,
      ],
      'Metric'  : [
        Unit.Milligram,
        Unit.Gram,
        Unit.Kilogram
      ]
    },
    'Volume'          : {
      'Imperial': [
        Unit.Pint,
        Unit.Gallon
      ],
      'Metric'  : [
        Unit.Liter
      ]
    },
    'Temperature'     : [
      Unit.Celsius,
      Unit.Fahrenheit,
      Unit.Kelvin
    ],
    'Other'           : [
      Unit.Percentage,
    ]
  };

  getPreviewValues() {
    const name = this.props.field.get('name');
    const value = this.props.field.get('value');
    const units = this.props.field.get('units');

    return {
      name : name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' && units ? `${value} ${Unit[units].abbreviation}` : null
    };
  }

  component() {

    return (
      <div>
        <Field title="Number"
               className={`metadata-name-field-${this.props.field.get('id')} normal-offset`}
               onFocus={this.onFocus}
               onBlur={this.onBlur}
               validateOnBlur={true}
               name={`metaFields.${this.props.metaFieldKey}.name`}
               placeholder="Field Name"
               validate={[Validation.Rules.required]}
        />

        <Row>
          <Col span={7}>

            <FormItem offset={false}>
              <FormItem.TitleGroup>
                <FormItem.Title style={{width: '50%'}}>Value</FormItem.Title>
                <FormItem.Title style={{width: '50%'}}>Units</FormItem.Title>
              </FormItem.TitleGroup>
              <FormItem.Content>
                <Input.Group compact>
                  <MetadataFormField style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                     name={`metaFields.${this.props.metaFieldKey}.value`} type="text"
                                     placeholder="Default val..." validate={[
                    Validation.Rules.number
                  ]}/>
                  <MetadataFormSelect style={{width: '50%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                      name={`metaFields.${this.props.metaFieldKey}.units`} type="text"
                                      placeholder="Choose"
                                      dropdownClassName="product-metadata-item-unit-dropdown" values={this.Unit}/>
                </Input.Group>
              </FormItem.Content>
            </FormItem>
          </Col>
          <Col span={9} offset={1}>
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
          <Col span={6} offset={1}>
            <Field title="Step"
                   className={`metadata-name-field-${this.props.field.get('id')} normal-offset`}
                   onFocus={this.onFocus}
                   onBlur={this.onBlur}
                   validateOnBlur={true}
                   name={`metaFields.${this.props.metaFieldKey}.step`}
                   placeholder="Optional"
            />
          </Col>
        </Row>

      </div>
    );
  }
}

UnitField.Static = Static;

export default UnitField;
