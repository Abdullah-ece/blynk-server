import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField, MetadataTime as MetadataFormTime} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import Static from './static';
import {Time} from 'services/Metadata';

class TimeField extends BaseField {

  getPreviewValues() {
    const name = this.props.field.get('name');
    const time = this.props.field.get('time');

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: !isNaN(Number(time)) ? `${Time.fromTimestamp(time)}` : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Time</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField className={`metadata-name-field-${this.props.field.get('id')}`}
                               validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text" placeholder="Field Name" validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
            <MetadataFormTime name={`metaFields.${this.props.metaFieldKey}.time`} type="text" timeFormat="HH:mm" placeholder="Choose Time"
                              timestampPicker={true}/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

TimeField.Static = Static;

export default TimeField;
