import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import {MetadataField as MetadataFormField, MetadataTime as MetadataFormTime} from 'components/Form';
import {formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import Validation from 'services/Validation';
import BaseField from '../BaseField';
import Static from './static';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      time: selector(state, 'time')
    }
  };
})
class TimeField extends BaseField {

  getPreviewValues() {
    const name = this.props.fields.name;
    const time = this.props.fields.time;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: time && typeof time === 'string' ? `${time}` : null
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
            <MetadataFormField name="name" type="text" placeholder="Field Name" validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
            <MetadataFormTime name="time" type="text" timeFormat="HH:mm" placeholder="Choose Time"/>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

TimeField.Static = Static;

export default TimeField;
