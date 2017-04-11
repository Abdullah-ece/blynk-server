import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {Menu, Button, Icon, Dropdown} from 'antd';

import {OrganizationFetch} from 'data/Organization/actions';

import './styles.less';
@connect((state) => ({
  Account: state.Account,
  Organization: state.Organization
}), (dispatch) => ({
  OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch)
}))
class Header extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    Account: React.PropTypes.object,
    Organization: React.PropTypes.object,
    OrganizationFetch: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    props.OrganizationFetch({
      id: props.Account.orgId
    });
  }

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
        <div className="user-layout--header-logo">
          <img src={ this.props.Organization.logoUrl || "assets/logo.png"} alt=""/>
        </div>
        <div className="user-layout--header-user">
          <div className="dark user-layout--header-user-link">
            { this.props.Account.email }
            <Dropdown overlay={this.AccountMenu()} trigger={['click']}>
              <Button type="primary" icon="user" className="user-layout--header-user-button dark"/>
            </Dropdown>
          </div>
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
