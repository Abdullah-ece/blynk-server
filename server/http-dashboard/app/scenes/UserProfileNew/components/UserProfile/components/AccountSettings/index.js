import React from 'react';
import {Section, Item} from '../../../Section';
import {Button, Modal, message} from 'antd';
import Field from '../../../Field';

import './styles.less';

class MyAccount extends React.Component {

  static propTypes = {
    Account: React.PropTypes.object,
    AccountFetch: React.PropTypes.func,
    onAccountNameUpdate: React.PropTypes.func,
    onAccountSave : React.PropTypes.func,
    onAccountResetPassword: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleNameSave = this.handleNameSave.bind(this);
    this.resetPassword  = this.resetPassword.bind(this);
  }

  handleNameSave(name) {
    const hideUpdatingMessage = message.loading('Updating account name..', 0);
    this.props.onAccountNameUpdate(name);
    /** @todo track error */
    this.props.onAccountSave(Object.assign({}, this.props.Account, {name: name})).then(() => {
      hideUpdatingMessage();
    });
  }

  resetPassword() {
    this.props.onAccountResetPassword(Object.assign({}, {email: this.props.Account.email})).then(() => {
      this.showResetPasswordSuccessMessage();
    }).catch((err) => {
      this.showResetPasswordErrorMessage(err && err.error && err.message);
    });
  }

  showResetPasswordErrorMessage(error) {
    Modal.error({
      title: 'Error',
      content: error || 'Server error. Please contact server administrator',
      okText: 'Ok'
    });
  }

  showResetPasswordSuccessMessage() {
    Modal.success({
      title: 'Success',
      content: 'Check your email for further instructions.',
      okText: 'Ok'
    });
  }

  render() {
    return (
      <div className="user-profile">

        <Item title="Name">
          <Field value={this.props.Account.name} onChange={this.handleNameSave}/>
        </Item>
        <Item title="Email Address">
          {this.props.Account.email}
        </Item>

        <Section title="Change Password">
          <Item>
            <Button type="primary"
                    onClick={this.resetPassword}
                    loading={this.props.Account.resetPasswordProcessing}>
              Send password reset email
            </Button>
          </Item>
        </Section>
      </div>
    );
  }

}

export default MyAccount;
