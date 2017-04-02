import React from 'react';

import {Menu as AntMenu}  from 'antd';

import './styles.scss';

class Menu extends React.Component {

  render() {

    return (
      <div className="user-layout--menu">
        <AntMenu>
          <AntMenu.Item>My Account</AntMenu.Item>
          <AntMenu.Item>Organization Settings</AntMenu.Item>
          <AntMenu.Item>Billing</AntMenu.Item>
          <AntMenu.Divider />
          <AntMenu.Item>Logout</AntMenu.Item>
        </AntMenu>
      </div>
    );
  }

}

export default Menu;
