import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField, MetadataTime as MetadataFormTime} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import Static from './static';
import {TimeRange} from 'services/Metadata';

class ShiftField extends BaseField {

  getPreviewValues() {
    const name = this.props.field.get(`name`);
    const from = this.props.field.get(`from`);
    const to = this.props.field.get(`to`);

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: !isNaN(Number(to)) && !isNaN(Number(from)) ? `From ${TimeRange.fromMinutes(from)} to ${TimeRange.fromMinutes(to)}` : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Time Range</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>From</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>To</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <MetadataFormField className={`metadata-name-field-${this.props.field.get(`id`)}`}
                               validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text" placeholder="Field Name"
                               style={{width: '50%'}} validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
            <MetadataFormTime style={{width: '25%'}} name={`metaFields.${this.props.metaFieldKey}.from`} type="text" timeFormat="HH:mm" placeholder="06:00"/>
            <MetadataFormTime style={{width: '25%'}} name={`metaFields.${this.props.metaFieldKey}.to`} type="text" timeFormat="HH:mm" placeholder="07:00"/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

ShiftField.Static = Static;

export default ShiftField;
