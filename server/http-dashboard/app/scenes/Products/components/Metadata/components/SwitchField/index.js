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
      from: selector(state, 'from'),
      to: selector(state, 'to')
    }
  };
})
class SwitchField extends BaseField {

  getPreviewValues() {
    const name = this.props.fields.name;
    const from = this.props.fields.from;
    const to = this.props.fields.to;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: typeof from === 'string' && typeof to === 'string' ? `${from} / ${to}` : null
    };
  }

  component() {
    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>String</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Option A</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Option B</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField className={`metadata-name-field-${this.props.field.id}`}
                               onFocus={this.onFocus.bind(this)} onBlur={this.onBlur.bind(this)}
                               validateOnBlur={true} name="name" type="text" placeholder="Field Name"
                               validate={[
                                 Validation.Rules.required, Validation.Rules.metafieldName,
                               ]}/>
            <MetadataFormField onFocus={this.onFocus.bind(this)} onBlur={this.onBlur.bind(this)}
                               name="from" type="text" placeholder="Option A"
                               validate={[
                                 Validation.Rules.required
                               ]}/>

            <MetadataFormField onFocus={this.onFocus.bind(this)} onBlur={this.onBlur.bind(this)}
                               name="to" type="text" placeholder="Option B"
                               validate={[
                                 Validation.Rules.required
                               ]}/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }

}

SwitchField.Static = Static;
export default SwitchField;
