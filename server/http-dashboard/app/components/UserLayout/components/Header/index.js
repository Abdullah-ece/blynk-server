import React from 'react';

import {Link} from 'react-router';

import {Menu, Button, Icon, Dropdown} from 'antd';

import './styles.scss';

class Header extends React.Component {

  static AccountMenu() {
    return (
      <Menu>
        <Menu.Item key="0">
          <Link to="/account">My Account</Link>
        </Menu.Item>
        <Menu.Item key="1">
          <Link to="/">Organization Settings</Link>
        </Menu.Item>
        <Menu.Item key="2">
          <Link to="/">Billing</Link>
        </Menu.Item>
        <Menu.Divider />
        <Menu.Item key="3">
          <Link to="/">
            <Icon type="logout"/> Log out
          </Link>
        </Menu.Item>
      </Menu>
    );
  }

  render() {
    return (
      <div className="user-layout--header">
        <div className="user-layout--header-logo">Blynk Inc.</div>
        <div className="user-layout--header-user">
          <Dropdown overlay={this.constructor.AccountMenu()} trigger={['click']}>
            <a href="javascript:void(0)" className="dark user-layout--header-user-link">
              some.longemail@evilcoropoation.com
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
