import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import BaseField from '../BaseField';
import {Currency, Unit} from 'services/Products';
import FieldStub from 'scenes/Products/components/FieldStub';

class CostField extends BaseField.Static {

  Currency = [
    Currency.USD,
    Currency.EUR,
    Currency.GBP,
    Currency.CNY,
    Currency.RUB
  ];

  getPreviewValues() {
    const fieldName = this.props.name;
    const perValue = this.props.perValue;
    const price = this.props.price;
    const units = this.props.units;
    const currency = this.props.currency;

    let name = null;
    let value = null;

    if (fieldName) {
      name = fieldName && typeof fieldName === 'string' ? `${fieldName.trim()}` : null;
    }

    if (price) {
      value = currency ? `${Currency[currency].abbreviation} ${price}` : null;
    }
    if (fieldName && price && perValue && units) {
      if (Number(perValue) === 1) {
        value = `${Currency[currency].abbreviation} ${price} / ${Unit[units].abbreviation}`;
      } else {
        value = `${Currency[currency].abbreviation} ${price} / ${perValue} ${Unit[units].abbreviation}`;
      }
    }

    return {
      name: name,
      value: value
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Cost of</FormItem.Title>
          <FormItem.Title style={{width: '12.5%'}}>CCY</FormItem.Title>
          <FormItem.Title style={{width: '12.5%'}}>Price</FormItem.Title>
          <FormItem.Title style={{width: '12.5%'}}>Per</FormItem.Title>
          <FormItem.Title style={{width: '12.5%'}}>Unit</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>

          <Input.Group compact>
            <FieldStub style={{width: '50%'}}>
              {this.props.name}
            </FieldStub>
            <FieldStub style={{width: '12.5%'}}>
              {this.props.currency}
            </FieldStub>
            <FieldStub style={{width: '12.5%'}}>
              {this.props.price || '--'}
            </FieldStub>
            <FieldStub style={{width: '12.5%'}}>
              {this.props.perValue || '--'}
            </FieldStub>
            <FieldStub style={{width: '12.5%'}}>
              {this.props.units || '--'}
            </FieldStub>
          </Input.Group>

        </FormItem.Content>
      </FormItem>
    );
  }
}

export default CostField;
