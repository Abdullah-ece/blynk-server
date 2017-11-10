import React from 'react';
import {reduxForm} from 'redux-form';
import {Form, Button} from 'antd';
import FormItem from 'components/FormItem';
import {Field as FormField, Select as FormSelect} from 'components/Form';
import Validation from 'services/Validation';
import {InviteAvailableRoles} from 'services/Roles';

import './styles.less';

@reduxForm({
  form: 'OrganizationSettingsInviteUsersForm',
  initialValues: {
    role: InviteAvailableRoles[0].key
  },
  touchOnBlur: false
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

  constructor(props) {
    super(props);

    this.state = {
      role: InviteAvailableRoles[0].key
    };
  }

  render() {
    const {invalid, pristine, handleSubmit, submitting} = this.props;

    return (
      <Form onSubmit={handleSubmit.bind(this)} layout="inline">
        <FormField title="Name" type="text" name="name"
                   icon="user"
                   placeholder="Enter name"
                   validate={[
                     Validation.Rules.required,
                     Validation.Rules.fullname,
                     Validation.Rules.minLength(3)
                   ]}/>

        <FormField title="Email" type="text" name="email"
                   icon="mail"
                   placeholder="Enter email"
                   validate={[
                     Validation.Rules.required,
                     Validation.Rules.email
                   ]}/>

        <FormSelect title="Role" type="text" name="role"
                    className="user-profile--organization-settings--invite-users-form-role-select"
                    values={InviteAvailableRoles} validate={[
          Validation.Rules.required,
        ]}/>

        <Form.Item>
          <FormItem>
            <FormItem.Title />
            <FormItem.Content>
              <Button type="primary" size="default" htmlType="submit"
                      loading={submitting}
                      disabled={invalid || pristine || submitting}>
                Invite
              </Button>
            </FormItem.Content>
          </FormItem>
        </Form.Item>
      </Form>
    );

  }

}

export default InviteUsersForm;
