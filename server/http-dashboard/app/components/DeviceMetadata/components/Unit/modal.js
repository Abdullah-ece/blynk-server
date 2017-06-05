import React from 'react';
import {reduxForm} from 'redux-form';
import {Input} from 'antd';
import {MetadataField, MetadataSelect} from 'components/Form';
import Validation from 'services/Validation';
import {Unit} from 'services/Products';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class UnitModal extends React.Component {

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
    ],
    'Other': [
      Unit.Percentage
    ]
  };

  render() {
    return (
      <div>
        <Input.Group compact>
          <MetadataSelect name="units" type="text" placeholder="Choose"
                          dropdownClassName="product-metadata-item-unit-dropdown" values={this.Unit}/>
          <MetadataField name="value" type="text" placeholder="Value" validate={[
            Validation.Rules.number
          ]}/>
        </Input.Group>
      </div>
    );
  }

}

export default UnitModal;
