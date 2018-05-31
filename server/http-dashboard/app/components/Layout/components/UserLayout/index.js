import {OrganizationFetch} from "data/Organization/actions";
import {OrganizationsFetch} from "data/Organizations/actions";
import React from 'react';
import {Menu, Icon, Avatar, Dropdown} from 'antd';
import {Link} from 'react-router';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as AccountActions from '../../../../data/Account/actions';
import {blynkWsConnect, blynkWsLogin} from 'store/blynk-websocket-middleware/actions';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';

import './styles.less';

@connect((state) => ({
  Account     : state.Account,
  Organization: state.Organization
}), (dispatch) => ({
  startLoading      : bindActionCreators(StartLoading, dispatch),
  finishLoading     : bindActionCreators(FinishLoading, dispatch),
  fetchAccount      : bindActionCreators(AccountActions.Account, dispatch),
  blynkWsConnect    : bindActionCreators(blynkWsConnect, dispatch),
  blynkWsLogin      : bindActionCreators(blynkWsLogin, dispatch),
  OrganizationFetch : bindActionCreators(OrganizationFetch, dispatch),
  OrganizationsFetch: bindActionCreators(OrganizationsFetch, dispatch)
}))
class UserLayout extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    Account           : React.PropTypes.object,
    children          : React.PropTypes.object,
    location          : React.PropTypes.object,
    fetchAccount      : React.PropTypes.func,
    blynkWsConnect    : React.PropTypes.func,
    blynkWsLogin      : React.PropTypes.func,
    Organization      : React.PropTypes.object,
    startLoading      : React.PropTypes.func,
    finishLoading     : React.PropTypes.func,
    OrganizationFetch : React.PropTypes.func,
    OrganizationsFetch: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      collapsed: true,
      current  : props.location.pathname
    };

    props.OrganizationFetch({
      id: props.Account.orgId
    });

    props.fetchAccount();

    this.handleMouseEnter = this.handleMouseEnter.bind(this);
    this.handleMouseLeave = this.handleMouseLeave.bind(this);
  }

  componentWillMount() {
    this.props.blynkWsConnect().then(() => {
      this.props.blynkWsLogin({
        username: this.props.Account.credentials.username,
        hash    : this.props.Account.credentials.password
      });
    });
  }

  componentWillReceiveProps(props) {
    this.setState({
      current: props.location.pathname
    });
  }

  toggleCollapsed = () => {
    this.setState({
      collapsed: !this.state.collapsed,
    });
  };

  handleClick(e) {
    // if (e.key === '/organizations') {

    // this.props.startLoading();
    // this.props.OrganizationsFetch().then(() => {
    // this.props.finishLoading();
    // this.context.router.push(e.key);
    // });

    // } else {
    this.context.router.push(e.key);
    // }
  }

  currentActivePage(state) {
    const splitedPath = state.split('/');

    if ('products' === splitedPath[1] || 'product' === splitedPath[1] )
      return ['/products'];

    if ('devices' === splitedPath[1])
      return ['/devices'];

    if ('organizations' === splitedPath[1])
      return ['/organizations'];
  }

  handleMouseEnter() {
    this.setState({
      'navigationActive': true,
    });
  }

  handleMouseLeave() {
    this.setState({
      'navigationActive': false,
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

  render() {

    return (
      <div className="user-layout">
        <div
          className={`user-layout-left-navigation-stack ${this.state.collapsed ? 'user-layout-left-navigation-stack-fold' : 'user-layout-left-navigation-stack-unfold'}`}>
          <div
            className={`user-layout-left-navigation ${this.state.navigationActive ? 'user-layout-left-navigation-active' : '' } ${this.state.collapsed ? 'user-layout-left-navigation-fold' : 'user-layout-left-navigation-unfold'}`}
            onMouseOver={this.handleMouseEnter}
            onMouseOut={this.handleMouseLeave}
          >
            <div className="user-layout-left-navigation-company-logo">
              <Link to="/">
                <img src={this.props.Organization.logoUrl} alt=""/>
              </Link>
            </div>
            <div className={`user-layout-left-navigation-collapse-btn`}>
              <Icon type={this.state.collapsed ? 'menu-unfold' : 'menu-fold'} onClick={this.toggleCollapsed}/>
            </div>
            <Menu
              onClick={this.handleClick.bind(this)}
              className={`user-layout-left-navigation-menu`}
              mode="inline"
              inlineCollapsed={this.state.collapsed}
              selectedKeys={this.currentActivePage(this.state.current)}
            >
              <Menu.Item key="/devices">
                <Icon type="hdd"/>
                <span>Devices</span>
              </Menu.Item>
              <Menu.Item key="/products">
                <Icon type="appstore-o"/>
                <span>Products</span>
              </Menu.Item>
              <Menu.Item key="/organizations">
                <Icon type="usergroup-add"/>
                <span>Organizations</span>
              </Menu.Item>
            </Menu>
            <div className="user-layout-left-navigation-profile">
              <Dropdown overlay={this.AccountMenu()} trigger={['click']} placement="topLeft">
                <Avatar size="large" icon="user" className="user-layout-left-navigation-profile-button"/>
              </Dropdown>
              <div>
                {!this.state.collapsed && this.props.Account.name}
              </div>
            </div>
          </div>
        </div>
        <div className={`user-layout-right-content ${this.state.collapsed ? 'user-layout-right-content-fold' : 'user-layout-right-content-unfold'}`}>
          {this.props.children}
        </div>
      </div>
    );
  }

}

export default UserLayout;
