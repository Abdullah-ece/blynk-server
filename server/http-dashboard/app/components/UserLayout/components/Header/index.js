import React from 'react';

import {Menu} from 'antd';

import './styles.scss';

class Header extends React.Component {

  render() {
    return (
      <div className="user-layout--header">
        <div className="user-layout--header-logo">Blynk Inc.</div>
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
