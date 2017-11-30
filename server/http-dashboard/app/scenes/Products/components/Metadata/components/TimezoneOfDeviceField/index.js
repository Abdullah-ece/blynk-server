import React      from 'react';
import _          from 'lodash';
import FormItem   from 'components/FormItem';
import {
  MetadataSelect
}                 from 'components/Form';
import {
  formValueSelector
}                 from 'redux-form';
import {
  connect
}                 from 'react-redux';
import Timezones  from 'services/timeszones';
import BaseField  from '../BaseField/index';
import Static     from './static';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    fields: {
      name: selector(state, 'name'),
      value: selector(state, 'value')
    }
  };
})
class TimezoneOfDeviceField extends BaseField {

  getPreviewValues() {
    const name = this.props.fields.name;
    const value = this.props.fields.value;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
    };
  }

  component() {

    const timezones = _.map(Timezones, (value, key) => {
      return {
        key: key,
        value: value
      };
    });

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Device Timezone</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <MetadataSelect displayError={false} name="value" values={timezones}
                          placeholder="Choose timezone"
          />
        </FormItem.Content>
      </FormItem>
    );
  }

}

TimezoneOfDeviceField.Static = Static;
export default TimezoneOfDeviceField;
