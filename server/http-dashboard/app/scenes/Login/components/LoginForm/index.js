import React from 'react';

import {Button, Row, Col, Form, Alert} from 'antd';

import {reduxForm} from 'redux-form';

import {Field as FormField} from 'components/Form';

import Validation from 'services/Validation';

import './styles.scss';

@reduxForm({
  form: 'Login'
})
export default class LoginForm extends React.Component {

  static propTypes = {
    invalid: React.PropTypes.bool,
    pristine: React.PropTypes.bool,
    submitting: React.PropTypes.bool,
    handleSubmit: React.PropTypes.func,
    error: React.PropTypes.string
  };

  render() {

    const {invalid, pristine, handleSubmit, error, submitting} = this.props;

    const FormItem = Form.Item;

    return (
      <Row type="flex" justify="space-around" align="middle" className="login-page">
        <Col>
          <Form onSubmit={handleSubmit.bind(this)} className="login-form">

            <FormItem>
              <span className="form-header">Log in to Blynk</span>
            </FormItem>

            { error && <FormItem><Alert description={error} type="error"/></FormItem>}

            <FormField type="text" name="email"
                       icon="user"
                       placeholder="Email"
                       displayError={false}
                       validate={[
                         Validation.Rules.required,
                         Validation.Rules.email
                       ]}/>

            <FormField type="password" name="password"
                       icon="lock"
                       placeholder="Password"
                       displayError={false}
                       validate={[
                         Validation.Rules.required
                       ]}/>

            <FormItem>
              <Button type="primary" htmlType="submit" className="login-form-button"
                      disabled={invalid || pristine || submitting}>
                Log in
              </Button>

            </FormItem>
            <FormItem>
              <a className="login-form-forgot">Forgot password?</a>
            </FormItem>
          </Form>
        </Col>
      </Row>
    );
  }
}
