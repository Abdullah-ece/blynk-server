import React from 'react';
import {reduxForm, formValueSelector, Field} from 'redux-form';
import {connect} from 'react-redux';
import {Button, Form, Checkbox} from 'antd';
import {Link} from 'react-router';
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
  form: 'InviteCreatePasswordForm',
  validate
})
@connect((state) => {
  const selector = formValueSelector('InviteCreatePasswordForm');
  return {
    conditionsAgreement: selector(state, 'conditionsAgreement'),
  };
}, () => {
  return {};
})
export default class InviteForm extends React.Component {

  static propTypes = {
    invalid: React.PropTypes.bool,
    pristine: React.PropTypes.bool,
    submitting: React.PropTypes.bool,
    handleSubmit: React.PropTypes.func,
    conditionsAgreement: React.PropTypes.any,
    error: React.PropTypes.string,
    loading: React.PropTypes.bool,
  };
  checkboxRender(props) {

    return (
      <Checkbox onChange={props.input.onChange} checked={props.input.value || false} className="login-form-checkbox">
        Accept&nbsp;
        <Link to="/terms-and-conditions" target="_blank">
          Terms Of Service
        </Link>
      </Checkbox>
    );
  }
  render() {
    const {invalid, pristine, error, submitting, handleSubmit} = this.props;

    const FormItem = Form.Item;

    return (<Form onSubmit={handleSubmit.bind(this)}>
      <div className="form-header">Create password</div>

      <div className="login-error">{ error && error }</div>

      <FormField type="password" name="password"
                 icon="lock"
                 placeholder="New password"
                 displayError={false}
                 validate={[
                   Validation.Rules.required
                 ]}/>

      <FormField type="password" name="passwordCopy"
                 icon="lock"
                 placeholder="Repeat password"
                 displayError={true}
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
          Save
        </Button>

      </FormItem>
    </Form>);
  }
}
