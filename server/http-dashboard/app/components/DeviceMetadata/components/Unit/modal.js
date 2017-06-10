import React from 'react';
import {reduxForm} from 'redux-form';
import {ItemsGroup, Item} from 'components/UI';
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
        <ItemsGroup>
          <Item label="Units" offset="normal">
            <MetadataSelect name="units" type="text" placeholder="Units"
                            dropdownClassName="product-metadata-item-unit-dropdown" values={this.Unit}
                            style={{width: '100%'}}/>
          </Item>
          <Item label="Value" offset="normal">
            <MetadataField name="value" type="text" placeholder="Value" validate={[
              Validation.Rules.number,
              Validation.Rules.required
            ]} style={{width: '100%'}}/>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default UnitModal;
