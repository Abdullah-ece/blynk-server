import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import Static from './static';

class TextField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);

  }

  getPreviewValues() {

    const name = this.props.field.get('name');
    const value = this.props.field.get('value');

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
    };
  }

  component() {
    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>String</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Value (optional)</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField className={`metadata-name-field-${this.props.field.get('id')}`}
                               onFocus={this.onFocus} onBlur={this.onBlur}
                               validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text" placeholder="Field Name" validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
            <MetadataFormField onFocus={this.onFocus} onBlur={this.onBlur}
                               name={`metaFields.${this.props.metaFieldKey}.value`} type="text" placeholder="Default value(optional)"/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }

}

TextField.Static = Static;
export default TextField;
