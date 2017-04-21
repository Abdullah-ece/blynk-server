import React from 'react';

import {Menu as AntMenu}  from 'antd';

import './styles.less';

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
                 selectedKeys={menuItemActive}>
          <AntMenu.Item key="/account">My Account</AntMenu.Item>
          <AntMenu.Item key="/organization-settings">Organization Settings</AntMenu.Item>
          {/*<AntMenu.Item key="/billing">Billing</AntMenu.Item>*/}
          <AntMenu.Divider />
          <AntMenu.Item key="/logout">Logout</AntMenu.Item>
        </AntMenu>
      </div>
    );
  }

}

export default Menu;
