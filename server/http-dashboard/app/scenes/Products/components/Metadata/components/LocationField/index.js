import React from 'react';
import FormItem from 'components/FormItem';
import {MetadataField as MetadataFormField} from 'components/Form';
import {formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
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
class LocationField extends BaseField {

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
          <FormItem.Title style={{width: '50%'}}>Location Name</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <MetadataFormField onFocus={this.onFocus.bind(this)} onBlur={this.onBlur.bind(this)}
                             name="value" type="text" placeholder="Default value(optional)"/>
        </FormItem.Content>
      </FormItem>
    );
  }

}

LocationField.Static = Static;
export default LocationField;
