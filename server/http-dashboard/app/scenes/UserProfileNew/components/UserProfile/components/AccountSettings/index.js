import React from 'react';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {Section, Item} from '../../../Section';
import {Button, Modal, message} from 'antd';
import {
  Account as AccountFetch,
  AccountSave,
  updateName as AccountUpdateName,
  AccountResetPassword
} from 'data/Account/actions';
import Field from '../../../Field';

import './styles.less';

@connect((state) => ({
  Account: state.Account
}), (dispatch) => ({
  AccountFetch: bindActionCreators(AccountFetch, dispatch),
  AccountSave: bindActionCreators(AccountSave, dispatch),
  AccountUpdateName: bindActionCreators(AccountUpdateName, dispatch),
  AccountResetPassword: bindActionCreators(AccountResetPassword, dispatch)
}))
class MyAccount extends React.Component {

  static propTypes = {
    Account: React.PropTypes.object,
    AccountFetch: React.PropTypes.func,
    AccountSave: React.PropTypes.func,
    AccountUpdateName: React.PropTypes.func,
    AccountResetPassword: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.state = {
      resetPasswordProcessing: false
    };
  }

  handleNameSave(name) {
    const hideUpdatingMessage = message.loading('Updating account name..', 0);
    this.props.AccountUpdateName(name);
    /** @todo track error */
    this.props.AccountSave(Object.assign({}, this.props.Account, {name: name})).then(() => {
      hideUpdatingMessage();
    });
  }

  resetPassword() {
    this.setState({
      resetPasswordProcessing: true
    });
    this.props.AccountResetPassword(Object.assign({}, {email: this.props.Account.email})).then(() => {
      this.setState({
        resetPasswordProcessing: false
      });
      this.showResetPasswordSuccessMessage();
    }).catch((err) => {
      this.setState({
        resetPasswordProcessing: false
      });
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
    console.log();
    return (
      <div className="user-profile">

        <Item title="Name">
          <Field value={this.props.Account.name} onChange={this.handleNameSave.bind(this)}/>
        </Item>
        <Item title="Email Address">
          {this.props.Account.email}
        </Item>

        <Section title="Change Password">
          <Item>
            <Button type="primary"
                    onClick={this.resetPassword.bind(this)}
                    loading={this.state.resetPasswordProcessing}>
              Send password reset email
            </Button>
          </Item>
        </Section>
      </div>
    );
  }

}

export default MyAccount;
