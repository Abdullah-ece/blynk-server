import React, {Component} from 'react';
import UserProfileComponent from './components/UserProfile';
import {TABS} from 'services/UserProfile';
import {connect} from 'react-redux';
import {
  tabChange
} from 'data/UserProfile/actions';
import {bindActionCreators} from 'redux';

import {
  AccountSave,
  updateName as AccountUpdateName,
  AccountResetPassword
} from 'data/Account/actions';

@connect((state)=>{
  return {
    Account: state.Account,
    activeTab: state.UserProfile.activeTab,
  };
},(dispatch) => {
  return {
    onTabChange: bindActionCreators(tabChange, dispatch),
    AccountUpdateName: bindActionCreators(AccountUpdateName, dispatch),
    AccountSave: bindActionCreators(AccountSave, dispatch),
    AccountResetPassword: bindActionCreators(AccountResetPassword, dispatch),
  };
})
class UserProfile extends Component {

  static propTypes = {
    Account: React.PropTypes.object,
    params: React.PropTypes.object,
    activeTab: React.PropTypes.string,
    onTabChange: React.PropTypes.func,
    AccountUpdateName: React.PropTypes.func,
    AccountSave: React.PropTypes.func,
    AccountResetPassword: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleTabChange = this.handleTabChange.bind(this);
    this.handleAccountUpdateName = this.handleAccountUpdateName.bind(this);
    this.handleAccountSave = this.handleAccountSave.bind(this);
    this.handleAccountResetPassword = this.handleAccountResetPassword.bind(this);
  }

  componentWillMount() {

    if(this.props.params.tab) {
      this.handleTabChange(this.props.params.tab);
    }
  }
  handleTabChange(tab) {
    this.props.onTabChange(tab);
  }

  handleAccountUpdateName(name) {
    this.props.AccountUpdateName(name);
  }

  handleAccountSave(data) {
    return this.props.AccountSave(data);
  }

  handleAccountResetPassword(data) {
    return this.props.AccountResetPassword(data);
  }

  render() {
    const params = {
      activeTab: this.props.activeTab || TABS.ACCOUNT_SETTINGS.key
    };

    return(
      <UserProfileComponent params={params}
                            onTabChange={this.handleTabChange}
                            onAccountNameUpdate={this.handleAccountUpdateName}
                            onAccountSave={this.handleAccountSave}
                            onAccountResetPassword={this.handleAccountResetPassword}
                            Account={this.props.Account}/>
    );
  }
}

export default UserProfile;
