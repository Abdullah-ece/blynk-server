import React from 'react';
import {reduxForm} from 'redux-form';
import {Button, Form} from 'antd';

import {Field as FormField} from 'components/Form';
import Validation from 'services/Validation';

const validate = values => {
  const errors = {};
  if (values.password !== values.passwordCopy) {
    errors.passwordCopy = "Password doesn't match";
  }
  return errors;
};

@reduxForm({
  form: 'ResetPass',
  validate
})
export default class ResetPassForm extends React.Component {

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
      <div className="form-header">Password change</div>

      <div className="login-error">{ error && error }</div>

      <FormField type="password" name="password"
                 icon="lock"
                 placeholder="New password"
                 displayError={false}
                 validate={[
                   Validation.Rules.required
                 ]}/>

      <div className="password-copy">
        <FormField type="password" name="passwordCopy"
                   icon="lock"
                   placeholder="Repeat password"
                   displayError={true}
                   validate={[
                     Validation.Rules.required
                   ]}/>
      </div>

      <FormItem>
        <Button type="primary"
                size="default"
                loading={submitting || this.props.loading}
                htmlType="submit" className="login-form-button"
                disabled={invalid || pristine || submitting}>
          Save
        </Button>

      </FormItem>
    </Form>);
  }
}
