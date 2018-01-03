import React from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {Menu, Button, Icon, Dropdown} from 'antd';
import {Link} from 'react-router';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';

import {OrganizationFetch} from 'data/Organization/actions';
import {OrganizationsFetch} from 'data/Organizations/actions';

import './styles.less';
@connect((state) => ({
  Account: state.Account,
  Organization: state.Organization
}), (dispatch) => ({
  startLoading: bindActionCreators(StartLoading, dispatch),
  finishLoading: bindActionCreators(FinishLoading, dispatch),
  OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch),
  OrganizationsFetch: bindActionCreators(OrganizationsFetch, dispatch)
}))
class Header extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    Account: React.PropTypes.object,
    Organization: React.PropTypes.object,
    startLoading: React.PropTypes.func,
    finishLoading: React.PropTypes.func,
    OrganizationFetch: React.PropTypes.func,
    OrganizationsFetch: React.PropTypes.func,
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
        <Menu.Item key="/user-profile/account-settings">
          My Profile
        </Menu.Item>
        <Menu.Item key="/user-profile/organization-settings">
          Organization Settings
        </Menu.Item>
        <Menu.Item key="/user-profile/users">
          Users
        </Menu.Item>
        <Menu.Item key="/user-profile/branding">
          Branding
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
    if (e.key === '/organizations') {

      this.props.startLoading();
      this.props.OrganizationsFetch().then(() => {
        this.props.finishLoading();
        this.context.router.push(e.key);
      });

    } else {
      this.context.router.push(e.key);
    }
  }

  currentActivePage(state) {
    const splitedPath = state.split('/');
    const length = splitedPath.length;
    for(let i = 0; i < length; i++) {
      if ('products' === splitedPath[i]) {
        return ['/products'];
      }
      if ('devices' === splitedPath[i]) {
        return ['/devices'];
      }
      if ('organizations' === splitedPath[i]) {
        return ['/organizations'];
      }
    }
  }

  render() {
    return (
      <div className="user-layout--header">
        <div className="user-layout--header--fixed">
          <div className="user-layout--header-logo">
            <Link to="/">
              <img src={this.props.Organization.logoUrl} alt=""/>
            </Link>
          </div>
          <div className="user-layout--header-user">
            <div className="dark user-layout--header-user-link">
              { this.props.Account.name }
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
            { this.props.Organization && this.props.Organization.canCreateOrgs && (
              <Menu.Item key="/products">Products</Menu.Item>
            ) || (null)}
            { this.props.Organization && this.props.Organization.canCreateOrgs && (
              <Menu.Item key="/organizations">Organizations</Menu.Item>
            ) || (null)}
            {/*<Menu.Item key="/organizations">Organizations</Menu.Item>*/}
          </Menu>
        </div>
      </div>
    );
  }

}

export default Header;
