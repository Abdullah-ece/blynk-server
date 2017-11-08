import React, {Component} from 'react';
import {Section, Item} from '../../../Section';
import InviteUsersForm from './components/InviteUsersForm';
import OrganizationUsers from './components/OrganizationUsers';
import {connect} from 'react-redux';
import {SubmissionError, reset} from 'redux-form';
import {bindActionCreators} from 'redux';
import {
  OrganizationUsersFetch,
  OrganizationSendInvite
} from 'data/Organization/actions';

@connect((state) => ({
  Account: state.Account
}), (dispatch) => ({
  OrganizationUsersFetch: bindActionCreators(OrganizationUsersFetch, dispatch),
  OrganizationSendInvite: bindActionCreators(OrganizationSendInvite, dispatch),
  ResetForm: bindActionCreators(reset, dispatch)
}))
class Users extends Component {
  static propTypes = {
    OrganizationUsersFetch: React.PropTypes.func,
    OrganizationSendInvite: React.PropTypes.func,
    ResetForm: React.PropTypes.func,
    Account: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.handleInviteSubmit = this.handleInviteSubmit.bind(this);
  }

  handleInviteSubmit(values) {
    return this.props.OrganizationSendInvite({
      id: this.props.Account.orgId,
      email: values.email,
      name: values.name,
      role: values.role
    }).then(() => {
      this.props.OrganizationUsersFetch({
        id: this.props.Account.orgId
      });
      this.props.ResetForm('OrganizationSettingsInviteUsersForm');
      this.showInviteSuccess();
    }).catch((err) => {
      this.showInviteError(
        err.error.response.message || 'Error sending invite'
      );
      new SubmissionError(err);
    });
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
