import React from 'react';
import PropTypes from 'prop-types';
import {Form, Input, Icon} from 'antd';

import {Field as FormField} from 'redux-form';

export default class Field extends React.Component {

  static propTypes = {
    maxLength: PropTypes.number
  };

  constructor(props) {
    super(props);
    this.renderField = this.renderField.bind(this);
  }

  renderField({validateOnBlur = false, className, disabled = false, displayError = true, style, placeholder, input, type, icon, meta: {active, touched, error, warning}}) {

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

    if (validateOnBlur && active) {
      validateStatus = 'success';
      help = '';
    }

    return (
      <Form.Item validateStatus={validateStatus}
                 help={help}
                 style={style}
                 className={className}>
        <Input maxLength={this.props.maxLength || false} {...input} type={type} placeholder={placeholder} disabled={disabled}
               prefix={icon ? <Icon type={icon} className="form--field-icon"/> : null}/>
      </Form.Item>
    );
  }

  render() {
    let props = this.props;

    if (props && props.validateOnBlur && !props.onFocus) {
      props = {
        ...props,
        onFocus: () => (true)
      };
    }

    return (
      <FormField {...props} component={this.renderField}/>
    );
  }
}
