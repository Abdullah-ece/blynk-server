import React from 'react';

import {Button, Form, Checkbox} from 'antd';

import {Link} from 'react-router';

import {connect} from 'react-redux';

import {bindActionCreators} from 'redux';

import {reduxForm, Field, formValueSelector} from 'redux-form';

import {Field as FormField} from 'components/Form';

import Validation from 'services/Validation';

import {LoginPageTermsAgreement} from 'data/Storage/actions';

import './styles.less';


@reduxForm({
  form: 'Login'
})
@connect((state) => {
  const selector = formValueSelector('Login');
  return {
    conditionsAgreement: selector(state, 'conditionsAgreement'),
  };
}, (dispatch) => ({
  LoginPageTermsAgreement: bindActionCreators(LoginPageTermsAgreement, dispatch),
}))
export default class LoginForm extends React.Component {

  static propTypes = {
    invalid: React.PropTypes.bool,
    pristine: React.PropTypes.bool,
    submitting: React.PropTypes.bool,
    handleSubmit: React.PropTypes.func,
    error: React.PropTypes.string,
    loading: React.PropTypes.bool,
    router: React.PropTypes.object,
    conditionsAgreement: React.PropTypes.any,
    LoginPageTermsAgreement: React.PropTypes.func,
  };
  constructor(props) {
    super(props);
    this.checkboxRender = this.checkboxRender.bind(this);
  }
  forgotPassHandler() {
    this.props.router.push('/forgot-pass');
  }

  checkboxRender(props) {
    const onChange = (value) => {
      this.props.LoginPageTermsAgreement(value.target.checked);
      props.input.onChange(value);
    };

    return (
      <Checkbox onChange={onChange} checked={props.input.value} className="login-form-checkbox">
        Accept&nbsp;
        <Link to="/terms-and-conditions" target="_blank">
          Terms Of Service
        </Link>
      </Checkbox>
    );
  }
  render() {

    const {invalid, pristine, handleSubmit, error, submitting} = this.props;

    const FormItem = Form.Item;

    return (<Form onSubmit={handleSubmit.bind(this)}>
      <div className="form-header">
        <span >Log In</span>
      </div>

      <div className="login-error">{ error && error }</div>

      <FormField type="text" name="email"
                 autoFocus={true}
                 validateStatus={!error ? 'success' : 'error'}
                 icon="user"
                 placeholder="Email"
                 displayError={false}
                 validate={[
                   Validation.Rules.required,
                   Validation.Rules.email
                 ]}/>

      <FormField type="password" name="password"
                 validateStatus={!error ? 'success' : 'error'}
                 icon="lock"
                 placeholder="Password"
                 displayError={false}
                 validate={[
                   Validation.Rules.required
                 ]}/>
      <Field name="conditionsAgreement" component={this.checkboxRender}/>
      <FormItem>
        <Button type="primary"
                size="default"
                loading={submitting || this.props.loading}
                htmlType="submit" className="login-form-button"
                disabled={invalid || pristine || submitting || !this.props.conditionsAgreement}>
          Log in
        </Button>

      </FormItem>
      <FormItem>
        <a className="login-form-forgot" onClick={this.forgotPassHandler.bind(this)}>Forgot password?</a>
      </FormItem>
    </Form>);
  }
}
