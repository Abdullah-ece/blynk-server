import React from 'react';
import {reduxForm} from 'redux-form';
import {Button, Form} from 'antd';

import {Field as FormField} from 'components/Form';
import Validation from 'services/Validation';

@reduxForm({
  form: 'ForgotPass'
})
export default class ForgotPassForm extends React.Component {

  static propTypes = {
    invalid: React.PropTypes.bool,
    pristine: React.PropTypes.bool,
    submitting: React.PropTypes.bool,
    handleSubmit: React.PropTypes.func,
    error: React.PropTypes.string,
    loading: React.PropTypes.bool,
  };

  render() {
    const {invalid, pristine, error, submitting, handleSubmit} = this.props;

    const FormItem = Form.Item;

    return (<Form onSubmit={handleSubmit.bind(this)}>
      <div className="form-header">Forgot password?</div>

      <div className="login-error">{ error && error }</div>

      <FormField type="text" name="email"
                 icon="user"
                 placeholder="Email"
                 displayError={false}
                 validate={[
                   Validation.Rules.required,
                   Validation.Rules.email
                 ]}/>

      <FormItem>
        <Button type="primary"
                size="default"
                loading={submitting || this.props.loading}
                htmlType="submit" className="login-form-button"
                disabled={invalid || pristine || submitting}>
          Send
        </Button>

      </FormItem>
    </Form>);
  }
}
