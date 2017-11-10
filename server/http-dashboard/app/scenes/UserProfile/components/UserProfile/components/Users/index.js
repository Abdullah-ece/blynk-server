import React, {Component} from 'react';
import {Section, Item} from '../../../Section';
import InviteUsersForm from './components/InviteUsersForm';
import OrganizationUsers from './components/OrganizationUsers';
import {Modal, message} from 'antd';
import {SubmissionError} from 'redux-form';

class Users extends Component {
  static propTypes = {
    Account: React.PropTypes.object,
    onOrganizationUsersFetch: React.PropTypes.func,
    onOrganizationSendInvite: React.PropTypes.func,
    onResetForm: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleInviteSubmit = this.handleInviteSubmit.bind(this);
  }

  handleInviteSubmit(values) {
    return this.props.onOrganizationSendInvite({
      id: this.props.Account.orgId,
      email: values.email,
      name: values.name,
      role: values.role
    }).then(() => {
      this.props.onOrganizationUsersFetch({
        id: this.props.Account.orgId
      });
      this.props.onResetForm('OrganizationSettingsInviteUsersForm');
      this.showInviteSuccess();
    }).catch((err) => {
      this.showInviteError(
        err.error.response.message || 'Error sending invite'
      );
      new SubmissionError(err);
    });
  }

  showInviteError(message) {
    Modal.error({
      title: 'Ooops!',
      content: String(message)
    });
  }

  showInviteSuccess() {
    message.success('Invite has been sent to email!');
  }

  render() {
    return(
      <div>
        <Section title="Invite Users">
          <Item>
            <InviteUsersForm onSubmit={this.handleInviteSubmit}/>
          </Item>
        </Section>
        <Section title="Users">
          <Item>
            <OrganizationUsers/>
          </Item>
        </Section>
      </div>
    );
  }
}

export default Users;
