import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {Menu, Button, Icon, Dropdown} from 'antd';
import {Link} from 'react-router';

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
    location: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    props.OrganizationFetch({
      id: props.Account.orgId
    });

    this.state = {
      current: props.location.pathname
    };
  }

  componentWillReceiveProps(props) {
    this.setState({
      current: props.location.pathname
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
        {/*<Menu.Item key="/billing">*/}
        {/*Billing*/}
        {/*</Menu.Item>*/}
        <Menu.Divider className="user-layout--menu-divider"/>
        <Menu.Item key="logout">
          <Icon type="login"/> Log out
        </Menu.Item>
      </Menu>
    );
  }

  handleClick(e) {
    this.context.router.push(e.key);
  }

  currentActivePage(state) {
    if (state.indexOf('product') >= 0) {
      return ['/products'];
    }
    if (state.indexOf('devices') >= 0) {
      return ['/devices'];
    }
  }

  render() {
    return (
      <div className="user-layout--header">
        <div className="user-layout--header-logo">
          <Link to="/">
            <img src={this.props.Organization.logoUrl} alt=""/>
          </Link>
        </div>
        <div className="user-layout--header-user">
          <div className="dark user-layout--header-user-link">
            { this.props.Account.email }
            <Dropdown overlay={this.AccountMenu()} trigger={['click']}>
              <Button type="primary" icon="user" className="user-layout--header-user-button dark"/>
            </Dropdown>
          </div>
        </div>
        <Menu mode="horizontal"
              className="user-layout--header-menu"
              onClick={this.handleClick.bind(this)}
              selectedKeys={this.currentActivePage(this.state.current)}>
          {/*<Menu.Item key="/dashboard">Dashboard</Menu.Item>*/}
          <Menu.Item key="/devices">Devices</Menu.Item>
          <Menu.Item key="/products">Products</Menu.Item>
          {/*<Menu.Item key="/organizations">Organizations</Menu.Item>*/}
        </Menu>
      </div>
    );
  }

}

export default Header;
