import React from 'react';

import {Form, Input, Icon} from 'antd';

import {Field as FormField} from 'redux-form';

import "./styles.less";

export default class Field extends React.Component {
  renderField({displayError = true, placeholder, rows, input, type, icon, meta: {touched, error, warning}}) {
    return (
      <Form.Item validateStatus={touched && displayError ? (error ? 'error' : warning ? 'warning' : '' ) : 'success'}
                 className="form-field"
                 help={touched && displayError ? (error || warning ? error || warning : '' ) : ''}>
        <Input {...input} rows={rows} type={type} placeholder={placeholder}
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
