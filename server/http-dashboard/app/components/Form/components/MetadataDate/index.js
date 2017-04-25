import React from 'react';

import {Form, DatePicker} from 'antd';
import {Field as FormField} from 'redux-form';
import moment from 'moment';

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

        <DatePicker
          format={timeFormat}
          style={{width: '100%'}}
          onChange={(moment, timeString) => input.onChange(timeString)}
          placeholder={placeholder}
          value={input.value ? moment(input.value, timeFormat) : defaultValue ? moment(defaultValue, timeFormat) : undefined}
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
