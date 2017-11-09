import React, {Component} from 'react';
import {Tabs} from 'antd';
import {MainLayout} from 'components';
import {
  TABS,
  getTabValueByKey
} from 'services/UserProfile';
import { browserHistory } from 'react-router';

import MyAccount from './components/AccountSettings';
import OrganizationSettings from './components/OrganizationSettings';
import Users from './components/Users';
import Branding from './components/Branding';

class UserProfile extends Component {

  static propTypes = {
    params: React.PropTypes.object,
    Account: React.PropTypes.object,
    onTabChange: React.PropTypes.func,
    onAccountNameUpdate: React.PropTypes.func,
    onAccountSave: React.PropTypes.func,
    onAccountResetPassword: React.PropTypes.func,
  };

  handleTabChange(tab){
    this.props.onTabChange(tab);
    browserHistory.push('/UserProfile/'+tab);
  }

  render() {
    const title = getTabValueByKey(this.props.params.activeTab);

    return(
      <MainLayout>
        <MainLayout.Header title={title}/>
        <MainLayout.Content className="product-create-content">
          <Tabs defaultActiveKey={TABS.ACCOUNT_SETTINGS.key}
                activeKey = {this.props.params.activeTab}
                onChange={(tab)=>{this.handleTabChange(tab);}}>

            <Tabs.TabPane tab={<span>{TABS.ACCOUNT_SETTINGS.value}</span>} key={TABS.ACCOUNT_SETTINGS.key}>
              <MyAccount onAccountNameUpdate={this.props.onAccountNameUpdate}
                         onAccountSave={this.props.onAccountSave}
                         onAccountResetPassword={this.props.onAccountResetPassword}
                         Account={this.props.Account}/>
            </Tabs.TabPane>

            <Tabs.TabPane tab={<span>{TABS.ORGANIZATION_SETTINGS.value}</span>} key={TABS.ORGANIZATION_SETTINGS.key}>
              <OrganizationSettings/>
            </Tabs.TabPane>

            <Tabs.TabPane tab={<span>{TABS.USERS.value}</span>} key={TABS.USERS.key}>
              <Users/>
            </Tabs.TabPane>

            <Tabs.TabPane tab={<span>{TABS.BRANDING.value}</span>} key={TABS.BRANDING.key}>
              <Branding/>
            </Tabs.TabPane>
          </Tabs>

        </MainLayout.Content>
      </MainLayout>
    );
  }
}

export default UserProfile;
