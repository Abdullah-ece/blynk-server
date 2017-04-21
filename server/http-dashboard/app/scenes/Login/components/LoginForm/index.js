import React from 'react';

import {Button, Form} from 'antd';

import {reduxForm} from 'redux-form';

import {Field as FormField} from 'components/Form';

import Validation from 'services/Validation';

import './styles.less';

@reduxForm({
  form: 'Login'
})
export default class LoginForm extends React.Component {

  static propTypes = {
    invalid: React.PropTypes.bool,
    pristine: React.PropTypes.bool,
    submitting: React.PropTypes.bool,
    handleSubmit: React.PropTypes.func,
    error: React.PropTypes.string,
    loading: React.PropTypes.bool,
    router: React.PropTypes.object
  };

  forgotPassHandler() {
    this.props.router.push('/forgot-pass');
  }

  render() {

    const {invalid, pristine, handleSubmit, error, submitting} = this.props;

    const FormItem = Form.Item;

    return (<Form onSubmit={handleSubmit.bind(this)}>
      <span className="form-header">Log In</span>

      <div className="login-error">{ error && error }</div>

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
        <Button type="primary"
                size="default"
                loading={submitting || this.props.loading}
                htmlType="submit" className="login-form-button"
                disabled={invalid || pristine || submitting}>
          Log in
        </Button>

      </FormItem>
      <FormItem>
        <a className="login-form-forgot" onClick={this.forgotPassHandler.bind(this)}>Forgot password?</a>
      </FormItem>
    </Form>);
  }
}
