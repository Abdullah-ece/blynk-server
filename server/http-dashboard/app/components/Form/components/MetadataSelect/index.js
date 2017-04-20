import React from 'react';

import {Form, Select} from 'antd';
import _ from 'lodash';
import {Field as FormField} from 'redux-form';

export default class Field extends React.Component {
  renderField({displayError = true, values, defaultValue, style, placeholder, input, meta: {touched, error, warning}}) {

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
        <Select
          showSearch
          style={{width: '100%'}}
          onChange={input.onChange}
          placeholder={placeholder}
          optionFilterProp="children"
          value={input.value ? input.value : defaultValue ? defaultValue : undefined}
          filterOption={(input, option) => option.props.stringValue.toLowerCase().indexOf(input.toLowerCase()) >= 0}
        >
          { _.map(values, (value, key) => (
            <Select.Option key={value} value={value} stringValue={key}>{key}</Select.Option>
          ))}
        </Select>
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
