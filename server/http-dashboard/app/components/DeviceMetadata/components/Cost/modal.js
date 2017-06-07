import React from 'react';
import {reduxForm} from 'redux-form';
import {Input} from 'antd';
import {MetadataSelect, Number} from 'components/Form';
import Validation from 'services/Validation';
import {Unit, Currency} from 'services/Products';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class CostModal extends React.Component {

  Currency = [
    Currency.USD,
    Currency.EUR,
    Currency.GBP,
    Currency.CNY,
    Currency.RUB
  ];

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
          <MetadataSelect name="currency" type="text" placeholder="Currency" values={this.Currency}/>

          <Number name="price" type="text" placeholder="Price" validate={[
            Validation.Rules.number
          ]}/>

          <Number name="perValue" type="text" placeholder="Per Value" validate={[
            Validation.Rules.number
          ]}/>

          <MetadataSelect name="units" type="text" placeholder="Units"
                          dropdownClassName="product-metadata-item-unit-dropdown"
                          values={this.Unit}/>
        </Input.Group>
      </div>
    );
  }

}

export default CostModal;
