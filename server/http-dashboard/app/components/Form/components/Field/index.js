import React from 'react';
import FormItem from 'components/FormItem';
import {Form, Input, Icon} from 'antd';

import {Field as FormField} from 'redux-form';

import "./styles.less";

export default class Field extends React.Component {

  static propTypes = {
    title: React.PropTypes.string
  };

  static getValidateStatus({validateOnBlur, displayError, input, meta: {active, touched, error, warning}}) {

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

    return {
      validateStatus,
      help
    };

  }

  titledField({validateOnBlur = true, autoFocus = false, style = {}, className = '', title, displayError = true, placeholder, rows, input, type, icon, meta: {touched, error, warning}}) {

    const {validateStatus, help} = Field.getValidateStatus({
      validateOnBlur,
      displayError,
      input,
      meta: {
        touched,
        error,
        warning
      }
    });

    return (
      <Form.Item validateStatus={validateStatus}
                 className={`form-field ${className}`}
                 style={style}
                 help={help}>
        <FormItem>
          <FormItem.Title>
            { title }
          </FormItem.Title>
          <FormItem.Content>
            <Input autoFocus={autoFocus || null} {...input} rows={rows} type={type} placeholder={placeholder}
                   prefix={icon ? <Icon type={icon} className="form--field-icon"/> : null}/>
          </FormItem.Content>
        </FormItem>
      </Form.Item>
    );
  }

  simpleField({validateOnBlur = true, style = {}, className = '', autoFocus = false, displayError = true, placeholder, rows, input, type, icon, meta: {touched, error, warning}}) {

    const {validateStatus, help} = Field.getValidateStatus({
      validateOnBlur,
      displayError,
      input,
      meta: {
        touched,
        error,
        warning
      }
    });

    return (
      <Form.Item
        validateStatus={validateStatus}
        className={`form-field ${className}`}
        style={style}
        help={help}>
        <Input autoFocus={autoFocus || null} {...input} rows={rows} type={type} placeholder={placeholder}
               prefix={icon ? <Icon type={icon} className="form--field-icon"/> : null}/>
      </Form.Item>
    );
  }


  render() {
    const props = this.props;

    if (this.props.title) {
      return (
        <FormField {...props} component={this.titledField}/>
      );
    } else {
      return (
        <FormField {...props} component={this.simpleField}/>
      );
    }
  }
}
