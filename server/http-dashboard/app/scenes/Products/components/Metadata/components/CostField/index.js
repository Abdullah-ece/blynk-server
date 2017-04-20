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
      currency: selector(state, 'currency')
    }
  };
})
@reduxForm({
  touchOnChange: true
})
export default class CostField extends React.Component {

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

  Currency = {
    'USD': '$',
    'EUR': '€',
    'GBP': '£',
    'CNY': '¥',
    'RUB': '₽'
  };

  getPreviewValues() {
    const name = this.props.fields.name;
    const value = this.props.fields.value;
    const currency = this.props.fields.currency;

    return {
      values: {
        name: name && typeof name === 'string' ? `${name.trim()}:` : null,
        value: value && typeof value === 'string' && currency ? `${currency} ${value}` : null
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
            <FormItem.Title style={{width: '50%'}}>Cost</FormItem.Title>
            <FormItem.Title style={{width: '25%'}}>Currency</FormItem.Title>
            <FormItem.Title style={{width: '25%'}}>Value</FormItem.Title>
          </FormItem.TitleGroup>
          <FormItem.Content>
            <Input.Group compact>
              <MetadataFormField name="name" type="text" placeholder="Field Name" style={{width: '200%'}} validate={[
                Validation.Rules.required
              ]}/>
              <MetadataFormSelect name="currency" type="text" placeholder="Choose" values={this.Currency}
                                  defaultValue={this.Currency.USD}/>
              <MetadataFormField name="value" type="text" placeholder="Default val..." validate={[
                Validation.Rules.number
              ]}/>
            </Input.Group>
          </FormItem.Content>
        </FormItem>
      </Metadata.Item>
    );
  }
}
