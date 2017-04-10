import React from 'react';
import {connect} from 'react-redux';
import {Menu, Button, Icon, Dropdown} from 'antd';

import './styles.less';
@connect((state) => ({
  Account: state.Account
}))
class Header extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    Account: React.PropTypes.object
  };

  AccountMenu() {

    const menuItemActive = [this.context.router.getCurrentLocation().pathname];

    return (
      <Menu onClick={this.handleClick.bind(this)}
            defaultSelectedKeys={menuItemActive}>
        <Menu.Item key="/account">
          My Account
        </Menu.Item>
        <Menu.Item key="/organization-settings">
          Organization Settings
        </Menu.Item>
        <Menu.Item key="/billing">
          Billing
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="logout">
          <Icon type="logout"/> Log out
        </Menu.Item>
      </Menu>
    );
  }

  handleClick(e) {
    this.context.router.push(e.key);
  }

  render() {
    return (
      <div className="user-layout--header">
        <div className="user-layout--header-logo">Blynk Inc.</div>
        <div className="user-layout--header-user">
          <Dropdown overlay={this.AccountMenu()} trigger={['click']}>
            <a href="javascript:void(0)" className="dark user-layout--header-user-link">
              { this.props.Account.email }
              <Button type="primary" icon="user" size="large" className="user-layout--header-user-button"/>
            </a>
          </Dropdown>
        </div>
        <Menu mode="horizontal" className="user-layout--header-menu">
          <Menu.Item>Dashboard</Menu.Item>
          <Menu.Item>Devices</Menu.Item>
          <Menu.Item>Products</Menu.Item>
          <Menu.Item>Organizations</Menu.Item>
        </Menu>
      </div>
    );
  }

}

export default Header;
