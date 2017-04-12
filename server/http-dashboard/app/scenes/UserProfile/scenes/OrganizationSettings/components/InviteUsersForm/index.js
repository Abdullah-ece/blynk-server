import React from 'react';
import {reduxForm, Field} from 'redux-form';
import {Form, Button, Select} from 'antd';
import {Field as FormField} from 'components/Form';
import Validation from 'services/Validation';
import {InviteAvailableRoles} from 'services/Roles';

import './styles.less';

@reduxForm({
  form: 'OrganizationSettingsInviteUsersForm',
  initialValues: {
    role: InviteAvailableRoles[0].value
  }
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
      role: InviteAvailableRoles[0].value
    };
  }

  getRolesListOptions() {
    const options = [];
    InviteAvailableRoles.forEach((role) => {
      options.push(<Select.Option key={role.value}>{role.title}</Select.Option>);
    });

    return options;
  }

  render() {
    const {invalid, pristine, handleSubmit, submitting} = this.props;

    const rolesOptions = this.getRolesListOptions();

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
          <Field name="role" component={(props) => {
            return (
              <Select onChange={(value) => {
                props.input.onChange(value);
                this.setState({role: value});
              }} defaultValue={InviteAvailableRoles[0].value}
                      value={this.state.role}
                      className="user-profile--organization-settings--invite-users-form-role-select">
                { rolesOptions }
              </Select>
            );
          }}/>
        </Form.Item>

        <Form.Item>
          <Button type="primary" size="default" htmlType="submit"
                  loading={submitting}
                  disabled={invalid || pristine || submitting}>
            Invite
          </Button>

        </Form.Item>
      </Form>
    );

  }

}

export default InviteUsersForm;
