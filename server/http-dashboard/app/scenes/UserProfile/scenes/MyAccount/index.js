import React from 'react';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import Title from '../../components/Title';
import {Section, Item} from '../../components/Section';
import {Button, Modal, Popconfirm, message} from 'antd';
import {
  Account as AccountFetch,
  AccountSave,
  updateName as AccountUpdateName,
  AccountResetPassword
} from 'data/Account/actions';
import Field from '../../components/Field';

import './styles.scss';

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
    AccountResetPassword: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      resetPasswordProcessing: false
    };
    props.AccountFetch();
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
    this.props.AccountResetPassword().then(() => {
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
      content: 'New password has been sent to your email!',
      okText: 'Ok'
    });
  }

  render() {

    return (
      <div className="user-profile">
        <Title text="My Account"/>
        <Section title="User Settings">
          <Item title="Name">
            <Field value={this.props.Account.name} onChange={this.handleNameSave.bind(this)}/>
          </Item>
          <Item title="Email Address">
            {this.props.Account.email}
          </Item>
        </Section>
        <Section title="Change Password">
          <Item>
            <Popconfirm title="Are you sure reset password?" onConfirm={this.resetPassword.bind(this)} okText="Yes"
                        cancelText="No">
              <Button type="primary"
                      loading={this.state.resetPasswordProcessing}>
                Send password reset email
              </Button>
            </Popconfirm>
          </Item>
        </Section>
      </div>
    );
  }

}

export default MyAccount;
