import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import Static from './static';

class SwitchField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);

  }

  getPreviewValues() {
    const name = this.props.field.get('name');
    const from = this.props.field.get('from');
    const to = this.props.field.get('to');

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: typeof from === 'string' && typeof to === 'string' ? `${from} / ${to}` : null
    };
  }

  component() {
    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '33.3%'}}>String</FormItem.Title>
          <FormItem.Title style={{width: '33.3%'}}>Option A</FormItem.Title>
          <FormItem.Title style={{width: '33.3%'}}>Option B</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField style={{width: '33.3%'}} className={`metadata-name-field-${this.props.field.get('id')}`}
                               onFocus={this.onFocus} onBlur={this.onBlur}
                               validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text" placeholder="Field Name"
                               validate={[
                                 Validation.Rules.required, Validation.Rules.metafieldName,
                               ]}/>
            <MetadataFormField style={{width: '33.3%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                               name={`metaFields.${this.props.metaFieldKey}.from`} type="text" placeholder="Option A"
                               validate={[
                                 Validation.Rules.required
                               ]}/>

            <MetadataFormField style={{width: '33.3%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                               name={`metaFields.${this.props.metaFieldKey}.to`} type="text" placeholder="Option B"
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
