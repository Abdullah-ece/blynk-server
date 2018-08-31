import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import Static from './static';

class NumberField extends BaseField {

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
      <div>
        <FormItem offset={false}>
          <FormItem.TitleGroup>
            <FormItem.Title style={{width: '50%'}}>Number</FormItem.Title>
            <FormItem.Title style={{width: '50%'}}>Value (optional)</FormItem.Title>
          </FormItem.TitleGroup>
          <FormItem.Content>
            <Input.Group compact>
              <MetadataFormField style={{width: '50%'}} className={`metadata-name-field-${this.props.field.get('id')}`}
                                 onFocus={this.onFocus} onBlur={this.onBlur}
                                 validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text" placeholder="Field Name" validate={[
                Validation.Rules.required, Validation.Rules.metafieldName,
              ]}/>
              <MetadataFormField style={{width: '50%'}} maxLength={15} onFocus={this.onFocus} onBlur={this.onBlur} name={`metaFields.${this.props.metaFieldKey}.value`}
                                 type="text" placeholder="Default value(optional)" validate={[
                Validation.Rules.number
              ]}/>
            </Input.Group>
          </FormItem.Content>
        </FormItem>
        <FormItem offset={false}>
          <FormItem.TitleGroup>
            <FormItem.Title style={{width: '50%'}}>Min/Max values (optional)</FormItem.Title>
          </FormItem.TitleGroup>
          <FormItem.Content>
            <Input.Group compact>

              <MetadataFormField style={{width: '20%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                 name={`metaFields.${this.props.metaFieldKey}.min`} type="text" placeholder="Min" validate={[
                Validation.Rules.number
              ]}/>
              <MetadataFormField style={{width: '20%'}} onFocus={this.onFocus} onBlur={this.onBlur}
                                 name={`metaFields.${this.props.metaFieldKey}.max`} type="text" placeholder="Max" validate={[
                Validation.Rules.number
              ]}/>
            </Input.Group>
          </FormItem.Content>
        </FormItem>
      </div>
    );
  }
}

NumberField.Static = Static;

export default NumberField;
