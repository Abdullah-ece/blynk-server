import React from 'react';
import FormItem from 'components/FormItem';
import {Form, InputNumber, Icon} from 'antd';
import PropTypes from 'prop-types';

import {Field as FormField} from 'redux-form';

export default class Number extends React.Component {

  static propTypes = {
    title: React.PropTypes.string,
    style: PropTypes.object,
  };

  titledField({style, title, displayError = true, placeholder, rows, input, type, icon, meta: {touched, error, warning}}) {
    return (
      <Form.Item style={style || {}} validateStatus={touched && displayError ? (error ? 'error' : warning ? 'warning' : '' ) : 'success'}
                 className="form-field"
                 help={touched && displayError ? (error || warning ? error || warning : '' ) : ''}>
        <FormItem>
          <FormItem.Title>
            { title }
          </FormItem.Title>
          <FormItem.Content>
            <InputNumber {...input} rows={rows} type={type} placeholder={placeholder}
                         prefix={icon ? <Icon type={icon} className="form--field-icon"/> : null}/>
          </FormItem.Content>
        </FormItem>
      </Form.Item>
    );
  }

  simpleField({style, displayError = true, placeholder, rows, input, type, icon, meta: {touched, error, warning}}) {
    return (
      <Form.Item style={style || {}} validateStatus={touched && displayError ? (error ? 'error' : warning ? 'warning' : '' ) : 'success'}
                 className="form-field"
                 help={touched && displayError ? (error || warning ? error || warning : '' ) : ''}>
        <InputNumber {...input} rows={rows} type={type} placeholder={placeholder}
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
