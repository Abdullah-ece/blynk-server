import React, { Component } from 'react';
import { Tabs, Icon } from 'antd';
import { MainLayout } from 'components';
import {
  TABS
} from 'services/UserProfile';

import MyAccount from './components/AccountSettings';
import OrganizationSettings from './components/OrganizationSettings';
import Users from './components/Users';
// import Branding from './components/Branding';
import { RolesAndPermissions } from 'scenes/UserProfile/scenes';

class UserProfile extends Component {

  static propTypes = {
    params: React.PropTypes.object,
    Account: React.PropTypes.object,
    Organization: React.PropTypes.object,
    onTabChange: React.PropTypes.func,
    onAccountNameUpdate: React.PropTypes.func,
    onAccountSave: React.PropTypes.func,
    onAccountResetPassword: React.PropTypes.func,

    GetPermissions: React.PropTypes.func,
    onOrganizationSave: React.PropTypes.func,
    onOrganizationLogoUpdate: React.PropTypes.func,
    onOrganizationUpdateName: React.PropTypes.func,
    onOrganizationBrandingUpdate: React.PropTypes.func,
    onOrganizationUpdateTimezone: React.PropTypes.func,
    onOrganizationUsersFetch: React.PropTypes.func,
    onOrganizationSendInvite: React.PropTypes.func,

    onResetForm: React.PropTypes.func,
  };

  handleTabChange(tab) {

    if (tab === 'roles-and-permissions') {
      this.props.GetPermissions().then(()=>{
        this.props.onTabChange(tab);
      });
    } else {
      this.props.onTabChange(tab);
    }
  }

  render() {

    return (
      <MainLayout>
        {!this.props.Organization.isLoading &&
        (<div>
          <MainLayout.Header title="Account Settings"/>
          <MainLayout.Content className="product-create-content">

            <Tabs defaultActiveKey={TABS.ACCOUNT_SETTINGS.key}
                  activeKey={this.props.params.activeTab}
                  onChange={(tab) => {this.handleTabChange(tab);}}>

              <Tabs.TabPane tab={<span>{TABS.ACCOUNT_SETTINGS.value}</span>}
                            key={TABS.ACCOUNT_SETTINGS.key}>
                <MyAccount onAccountNameUpdate={this.props.onAccountNameUpdate}
                           onAccountSave={this.props.onAccountSave}
                           onAccountResetPassword={this.props.onAccountResetPassword}
                           Account={this.props.Account}/>
              </Tabs.TabPane>

              <Tabs.TabPane
                tab={<span>{TABS.ORGANIZATION_SETTINGS.value}</span>}
                key={TABS.ORGANIZATION_SETTINGS.key}>
                <OrganizationSettings Account={this.props.Account}
                                      Organization={this.props.Organization}
                                      onOrganizationUpdateName={this.props.onOrganizationUpdateName}
                                      onOrganizationSave={this.props.onOrganizationSave}
                                      onOrganizationUpdateTimezone={this.props.onOrganizationUpdateTimezone}/>
              </Tabs.TabPane>

              <Tabs.TabPane tab={<span>{TABS.USERS.value}</span>}
                            key={TABS.USERS.key}>
                <Users Account={this.props.Account}
                       onOrganizationUsersFetch={this.props.onOrganizationUsersFetch}
                       onOrganizationSendInvite={this.props.onOrganizationSendInvite}
                       onResetForm={this.props.onResetForm}/>
              </Tabs.TabPane>

              {/*<Tabs.TabPane tab={<span>{TABS.BRANDING.value}</span>} key={TABS.BRANDING.key}>*/}
              {/*<Branding Organization={this.props.Organization}*/}
              {/*onOrganizationSave={this.props.onOrganizationSave}*/}
              {/*onOrganizationLogoUpdate={this.props.onOrganizationLogoUpdate}*/}
              {/*onOrganizationBrandingUpdate={this.props.onOrganizationBrandingUpdate}/>*/}
              {/*</Tabs.TabPane>*/}

              <Tabs.TabPane
                tab={<span>{TABS.ROLES_AND_PERMISSIONS.value}</span>}
                key={TABS.ROLES_AND_PERMISSIONS.key}>
                <RolesAndPermissions/>
              </Tabs.TabPane>
            </Tabs>
          </MainLayout.Content>
        </div>) || (<Icon type="loading" style={{ fontSize: 72 }}/>)}
      </MainLayout>
    );
  }
}

export default UserProfile;
