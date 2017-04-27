import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import BaseField from '../BaseField';
import {Currency} from 'services/Products';
import Static from './static';
import classnames from 'classnames';

class CostField extends BaseField.Static {

  Currency = [
    Currency.USD,
    Currency.EUR,
    Currency.GBP,
    Currency.CNY,
    Currency.RUB
  ];

  getPreviewValues() {
    const name = this.props.name;
    const value = this.props.value;
    const currency = this.props.currency;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}:` : null,
      value: value && typeof value === 'string' && currency ? `${Currency[currency].abbreviation} ${value}` : null
    };
  }

  component() {

    const valueClassNames = classnames({
      'product-metadata-static-field': true,
      'no-value': !this.props.value
    });

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Cost</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Currency</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>

          <Input.Group compact>
            <div className="product-metadata-static-field" style={{width: '200%'}}>
              {this.props.name}
            </div>
            <div className="product-metadata-static-field">
              {this.props.currency}
            </div>
            <div className={valueClassNames}>
              {this.props.value || this.DEFAULT_VALUE}
            </div>
          </Input.Group>

        </FormItem.Content>
      </FormItem>
    );
  }
}

CostField.Static = Static;

export default CostField;
