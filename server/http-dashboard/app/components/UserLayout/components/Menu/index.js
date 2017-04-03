import React from 'react';

import {Menu as AntMenu}  from 'antd';

import './styles.scss';

class Menu extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  handleClick(e) {
    this.context.router.push(e.key);
  }

  render() {

    const menuItemActive = [this.context.router.getCurrentLocation().pathname];

    return (
      <div className="user-layout--menu">
        <AntMenu onClick={this.handleClick.bind(this)}
                 defaultSelectedKeys={menuItemActive}>
          <AntMenu.Item key="/account">My Account</AntMenu.Item>
          <AntMenu.Item key="/organization-settings">Organization Settings</AntMenu.Item>
          <AntMenu.Item key="/billing">Billing</AntMenu.Item>
          <AntMenu.Divider />
          <AntMenu.Item key="/login">Logout</AntMenu.Item>
        </AntMenu>
      </div>
    );
  }

}

export default Menu;
