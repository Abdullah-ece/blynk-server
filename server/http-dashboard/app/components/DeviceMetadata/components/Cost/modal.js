import React from 'react';
import {ItemsGroup, Item, Number} from 'components/UI';
import {MetadataSelect} from 'components/Form';
import Validation from 'services/Validation';
import {Unit, Currency} from 'services/Products';

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

        <ItemsGroup>
          <Item label="Currency">
            <MetadataSelect name="currency" type="text" placeholder="Currency" values={this.Currency}
                            style={{width: '100%'}}/>
          </Item>
          <Item label="Price">
            <Number name="price" type="text" placeholder="Price" validate={[
              Validation.Rules.number,
              Validation.Rules.required
            ]} style={{width: '100%'}}/>
          </Item>
          <Item label="Per Value">
            <Number name="perValue" type="text" placeholder="Per Value" validate={[
              Validation.Rules.number,
              Validation.Rules.required
            ]} style={{width: '100%'}}/>
          </Item>
          <Item label="Units">
            <MetadataSelect name="units" type="text" placeholder="Units"
                            dropdownClassName="product-metadata-item-unit-dropdown"
                            values={this.Unit}
                            style={{width: '100%'}}/>
          </Item>
        </ItemsGroup>
      </div>
    );
  }

}

export default CostModal;
