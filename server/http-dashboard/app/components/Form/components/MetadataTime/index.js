import React from 'react';

import {Form, TimePicker} from 'antd';
import {Field as FormField} from 'redux-form';
import moment from 'moment';
import {TimeRange} from 'services/Metadata';

export default class Field extends React.Component {
  renderField({displayError = true, timeFormat, defaultValue, style, placeholder, input, meta: {touched, error, warning}}) {

    let validateStatus = 'success';
    let help = '';
    if (touched && displayError && error) {
      validateStatus = 'error';
      help = error || warning || '';
    }

    if (!touched && input.value && error) {
      validateStatus = 'error';
      help = error || warning || '';
    }

    return (
      <Form.Item validateStatus={validateStatus}
                 help={help}
                 style={style}>

        <TimePicker
          format={timeFormat}
          style={{width: '100%'}}
          onChange={(moment, timeString) => input.onChange(TimeRange.toMinutes(timeString))}
          placeholder={placeholder}
          value={input.value ? moment(TimeRange.fromMinutes(input.value), timeFormat) : defaultValue ? moment(TimeRange.fromMinutes(defaultValue), timeFormat) : moment('00:00', timeFormat)}
        />
      </Form.Item>
    );
  }

  render() {
    const props = this.props;
    return (
      <FormField {...props} component={this.renderField}/>
    );
  }
}
