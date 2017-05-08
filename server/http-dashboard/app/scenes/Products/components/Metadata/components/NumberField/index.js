import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField} from 'components/Form';
import {formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import Static from './static';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      value: selector(state, 'value')
    }
  };
})
class NumberField extends BaseField {

  getPreviewValues() {
    const name = this.props.fields.name;
    const value = this.props.fields.value;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Number</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Value (optional)</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField name="name" type="text" placeholder="Field Name" validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
            <MetadataFormField name="value" type="text" placeholder="Default value(optional)" validate={[
              Validation.Rules.number
            ]}/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

NumberField.Static = Static;

export default NumberField;
