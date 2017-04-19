import React from 'react';

import {Form, Input, Icon} from 'antd';

import {Field as FormField} from 'redux-form';

export default class Field extends React.Component {
  renderField({displayError = true, placeholder, input, type, icon, meta: {touched, error, warning}}) {

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
                 help={help}>
        <Input {...input} type={type} placeholder={placeholder}
               prefix={icon ? <Icon type={icon} className="form--field-icon"/> : null}/>
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
