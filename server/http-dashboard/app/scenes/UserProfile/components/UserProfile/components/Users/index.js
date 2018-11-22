import React, {Component} from 'react';
import {displayError} from "services/ErrorHandling";
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
      id: this.props.Account.selectedOrgId,
      email: values.email,
      name: values.name,
      roleId: Number(values.role),
    }).then(() => {
      this.props.onOrganizationUsersFetch({
        id: this.props.Account.selectedOrgId
      });
      this.props.onResetForm('OrganizationSettingsInviteUsersForm');
      this.showInviteSuccess();
    }).catch((err) => {
      this.showInviteError(
        displayError(err) || 'Error sending invite'
      );
      new SubmissionError(err);
    });
  }

  showInviteError(message) {
    Modal.warning({
      title: 'Error sending invite',
      content: String(message)
    });
  }

  showInviteSuccess() {
    message.success('Invite has been sent to email');
  }

  render() {
    return(
      <div>
        <Item>
          <InviteUsersForm onSubmit={this.handleInviteSubmit}/>
        </Item>
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
