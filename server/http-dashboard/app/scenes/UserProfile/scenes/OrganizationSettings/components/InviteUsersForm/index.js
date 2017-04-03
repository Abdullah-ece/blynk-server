import React from 'react';
import {reduxForm} from 'redux-form';
import {Form, Button, Select} from 'antd';
import {Field as FormField} from 'components/Form';
import Validation from 'services/Validation';

import './styles.scss';

@reduxForm({
  form: 'OrganizationSettingsInviteUsersForm'
})
class InviteUsersForm extends React.Component {

  static propTypes = {
    invalid: React.PropTypes.bool,
    pristine: React.PropTypes.bool,
    submitting: React.PropTypes.bool,
    handleSubmit: React.PropTypes.func,
    onSubmit: React.PropTypes.func,
    reset: React.PropTypes.func,
    error: React.PropTypes.string
  };

  render() {
    const {invalid, pristine, handleSubmit, submitting} = this.props;

    return (
      <Form onSubmit={handleSubmit.bind(this)} layout="inline">
        <FormField type="text" name="name"
                   icon="user"
                   placeholder="Enter name"
                   validate={[
                     Validation.Rules.required,
                     Validation.Rules.fullname,
                     Validation.Rules.minLength(3)
                   ]}/>

        <FormField type="text" name="email"
                   icon="mail"
                   placeholder="Enter email"
                   validate={[
                     Validation.Rules.required,
                     Validation.Rules.email
                   ]}/>

        <Form.Item>
          <Select defaultValue="Admin" className="user-profile--organization-settings--invite-users-form-role-select"
                  style={{width: 100}}>
            <Select.Option value="Admin">Admin</Select.Option>
            <Select.Option value="Manager">Manager</Select.Option>
            <Select.Option value="Read Only">Read Only</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item>
          <Button type="primary" size="default" htmlType="submit"
                  disabled={invalid || pristine || submitting}>
            Invite
          </Button>

        </Form.Item>
      </Form>
    );

  }

}

export default InviteUsersForm;
