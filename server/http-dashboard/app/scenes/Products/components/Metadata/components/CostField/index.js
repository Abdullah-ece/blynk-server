import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField, MetadataSelect as MetadataFormSelect} from 'components/Form';
import {formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import {Currency} from 'services/Products';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      value: selector(state, 'value'),
      currency: selector(state, 'currency')
    }
  };
})
export default class CostField extends BaseField {

  Currency = [
    Currency.USD,
    Currency.EUR,
    Currency.GBP,
    Currency.CNY,
    Currency.RUB
  ];

  getPreviewValues() {
    const name = this.props.fields.name;
    const value = this.props.fields.value;
    const currency = this.props.fields.currency;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}:` : null,
      value: value && typeof value === 'string' && currency ? `${currency} ${value}` : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Cost</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Currency</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField name="name" type="text" placeholder="Field Name" style={{width: '200%'}} validate={[
              Validation.Rules.required
            ]}/>
            <MetadataFormSelect name="currency" type="text" placeholder="Choose" values={this.Currency}/>
            <MetadataFormField name="value" type="text" placeholder="Default val..." validate={[
              Validation.Rules.number
            ]}/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}
