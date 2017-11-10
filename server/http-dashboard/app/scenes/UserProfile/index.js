import React, {Component} from 'react';
import UserProfileComponent from './components/UserProfile';
import {TABS} from 'services/UserProfile';
import {connect} from 'react-redux';
import {
  tabChange
} from 'data/UserProfile/actions';
import {bindActionCreators} from 'redux';

import {reset} from 'redux-form';

import {
  AccountSave,
  updateName as AccountUpdateName,
  AccountResetPassword
} from 'data/Account/actions';

import {
  OrganizationBrandingUpdate,
  OrganizationSave,
  OrganizationLogoUpdate,
  OrganizationUpdateName,
  OrganizationUpdateTimezone,
  OrganizationSendInvite,
  OrganizationUsersFetch,
} from 'data/Organization/actions';


@connect((state)=>{
  return {
    Account: state.Account,
    Organization: state.Organization,
    activeTab: state.UserProfile.activeTab,
  };
},(dispatch) => {
  return {
    onTabChange: bindActionCreators(tabChange, dispatch),
    AccountUpdateName: bindActionCreators(AccountUpdateName, dispatch),
    AccountSave: bindActionCreators(AccountSave, dispatch),
    AccountResetPassword: bindActionCreators(AccountResetPassword, dispatch),

    OrganizationBrandingUpdate: bindActionCreators(OrganizationBrandingUpdate, dispatch),
    OrganizationUpdateName: bindActionCreators(OrganizationUpdateName, dispatch),
    OrganizationLogoUpdate: bindActionCreators(OrganizationLogoUpdate, dispatch),
    OrganizationSave: bindActionCreators(OrganizationSave, dispatch),
    OrganizationUpdateTimezone: bindActionCreators(OrganizationUpdateTimezone, dispatch),
    OrganizationSendInvite: bindActionCreators(OrganizationSendInvite, dispatch),
    OrganizationUsersFetch: bindActionCreators(OrganizationUsersFetch, dispatch),

    ResetForm: bindActionCreators(reset, dispatch),
  };
})
class UserProfile extends Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    Account: React.PropTypes.object,
    Organization: React.PropTypes.object,
    params: React.PropTypes.object,
    activeTab: React.PropTypes.string,
    onTabChange: React.PropTypes.func,
    ResetForm: React.PropTypes.func,

    AccountUpdateName: React.PropTypes.func,
    AccountSave: React.PropTypes.func,
    AccountResetPassword: React.PropTypes.func,

    OrganizationBrandingUpdate: React.PropTypes.func,
    OrganizationUpdateName: React.PropTypes.func,
    OrganizationLogoUpdate: React.PropTypes.func,
    OrganizationSave: React.PropTypes.func,
    OrganizationUpdateTimezone: React.PropTypes.func,
    OrganizationUsersFetch: React.PropTypes.func,
    OrganizationSendInvite: React.PropTypes.func,

  };

  constructor(props) {
    super(props);

    this.handleTabChange = this.handleTabChange.bind(this);
    this.handleAccountUpdateName = this.handleAccountUpdateName.bind(this);
    this.handleAccountSave = this.handleAccountSave.bind(this);
    this.handleAccountResetPassword = this.handleAccountResetPassword.bind(this);
    this.handleOrganizationBrandingUpdate = this.handleOrganizationBrandingUpdate.bind(this);
    this.handleOrganizationUpdateName = this.handleOrganizationUpdateName.bind(this);
    this.handleOrganizationLogoUpdate = this.handleOrganizationLogoUpdate.bind(this);
    this.handleOrganizationSave = this.handleOrganizationSave.bind(this);
    this.handleOrganizationUpdateTimezone = this.handleOrganizationUpdateTimezone.bind(this);
    this.handleOrganizationUsersFetch = this.handleOrganizationUsersFetch.bind(this);
    this.handleOrganizationSendInvite = this.handleOrganizationSendInvite.bind(this);
  }

  componentWillMount() {

    if(this.props.params.tab) {
      this.handleTabChange(this.props.params.tab);
    }
  }
  handleTabChange(tab) {
    this.props.onTabChange(tab);
    this.context.router.push(`/user-profile/${tab}`);
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

  handleOrganizationBrandingUpdate(colours) {
    this.props.OrganizationBrandingUpdate(colours);
  }

  handleOrganizationLogoUpdate(logo) {
    this.props.OrganizationLogoUpdate(logo);
  }

  handleOrganizationSave(data) {
    return this.props.OrganizationSave(data);
  }

  handleOrganizationUpdateName(name) {
    this.props.OrganizationUpdateName(name);
  }

  handleOrganizationUpdateTimezone(timezone) {
    this.props.OrganizationUpdateTimezone(timezone);
  }

  handleOrganizationUsersFetch(data) {
    this.props.OrganizationUsersFetch(data);
  }

  handleOrganizationSendInvite(data) {
    return this.props.OrganizationSendInvite(data);
  }


  render() {
    const params = {
      activeTab: this.props.params.tab || TABS.ACCOUNT_SETTINGS.key
    };
    return(
      <UserProfileComponent params={params}
                            onTabChange={this.handleTabChange}
                            Account={this.props.Account}
                            onAccountNameUpdate={this.handleAccountUpdateName}
                            onAccountSave={this.handleAccountSave}
                            onAccountResetPassword={this.handleAccountResetPassword}

                            Organization={this.props.Organization}
                            onOrganizationSave={this.handleOrganizationSave}
                            onOrganizationLogoUpdate={this.handleOrganizationLogoUpdate}
                            onOrganizationUpdateName={this.handleOrganizationUpdateName}
                            onOrganizationBrandingUpdate={this.handleOrganizationBrandingUpdate}
                            onOrganizationUpdateTimezone={this.handleOrganizationUpdateTimezone}
                            onOrganizationUsersFetch = {this.handleOrganizationUsersFetch}
                            onOrganizationSendInvite = {this.handleOrganizationSendInvite}

                            onResetForm = {this.props.ResetForm}/>
    );
  }
}

export default UserProfile;
