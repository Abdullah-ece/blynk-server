import {OrganizationFetch} from "data/Organization/actions";
import {
  OrganizationsFetch,
  OrganizationsHierarchyFetch
} from "data/Organizations/actions";
import React from 'react';
import {Menu, Icon, Avatar, Dropdown} from 'antd';
import {LinearIcon} from "components";
import {Link} from 'react-router';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as AccountActions from 'data/Account/actions';
import {blynkWsConnect, blynkWsLogin} from 'store/blynk-websocket-middleware/actions';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';

import './styles.less';

@connect((state) => ({
  Account     : state.Account,
  Organization: state.Organization,
  hierarchy   : state.Organizations.get('hierarchy'),
}), (dispatch) => ({
  organizationsHierarchyFetch : bindActionCreators(OrganizationsHierarchyFetch, dispatch),
  startLoading      : bindActionCreators(StartLoading, dispatch),
  finishLoading     : bindActionCreators(FinishLoading, dispatch),
  fetchAccount      : bindActionCreators(AccountActions.Account, dispatch),
  selectOrgId       : bindActionCreators(AccountActions.AccountSelectOrgId, dispatch),
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
    selectOrgId       : React.PropTypes.func,
    blynkWsLogin      : React.PropTypes.func,
    Organization      : React.PropTypes.object,
    hierarchy         : React.PropTypes.object,
    startLoading      : React.PropTypes.func,
    finishLoading     : React.PropTypes.func,
    OrganizationFetch : React.PropTypes.func,
    OrganizationsFetch: React.PropTypes.func,
    organizationsHierarchyFetch: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      collapsed: true,
      current  : props.location.pathname
    };

    this.fetchData();

    props.fetchAccount();

    props.organizationsHierarchyFetch();

    this.OrgSelection = this.OrgSelection.bind(this);
    this.handleOrgSelect = this.handleOrgSelect.bind(this);
    this.handleMouseEnter = this.handleMouseEnter.bind(this);
    this.handleMouseLeave = this.handleMouseLeave.bind(this);
  }

  componentWillMount() {
    // this.props.blynkWsConnect().then(() => {
    //   this.props.blynkWsLogin({
    //     username: this.props.Account.credentials.username,
    //     hash    : this.props.Account.credentials.password
    //   });
    // });
  }

  componentWillReceiveProps(props) {
    this.setState({
      current: props.location.pathname
    });
  }

  componentDidUpdate(prevProps) {
    if(prevProps.Account.selectedOrgId !== this.props.Account.selectedOrgId)
      this.fetchData();
  }

  fetchData() {
    this.props.OrganizationFetch({
      id: this.props.Account.selectedOrgId
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

  handleOrgSelect(e) {
    this.props.selectOrgId({
      orgId: e.key
    });

    this.context.router.push('/devices');
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

  OrgSelection() {

    const renderChild = (child, level = 1) => {
      if(!child)
        return null;

      let children = [];

      if(child && child.get('childs') && child.get('childs').map) {
        child.get('childs').map((child) => {
          children.push(renderChild(child, level + 1));
        });
      }

      return [
        <Menu.Item key={(child.get('id')).toString()} className={`user-layout--organization-select--org-level-${level} ${isActive(child.get('id'))}`}>
          {child.get('name')}
        </Menu.Item>,
        children
      ];
    };

    const currentOrgId = this.props.Account.selectedOrgId;

    const hierarchy = this.props.hierarchy;

    const id = hierarchy && hierarchy.get && hierarchy.get('id');
    const name = hierarchy && hierarchy.get && hierarchy.get('name');
    const childs = hierarchy && hierarchy.get && hierarchy.get('childs');

    const isActive = (id) => {
      if(Number(id) === Number(currentOrgId)) {
        return 'user-layout--organization-select--org-active';
      }
      return '';
    };

    return (
      <Menu className="user-layout--organization-select" onClick={this.handleOrgSelect.bind(this)}>
        <Menu.ItemGroup title="Main Organization" className={`user-layout--organization-select--meta-org`}>
          <Menu.Item key={`${id}`}>
            {name}
          </Menu.Item>
        </Menu.ItemGroup>
        <Menu.Divider/>
        { childs && childs.size && (
          <Menu.ItemGroup title="Sub Organizations">
            { childs.map((child) => renderChild(child)) }
          </Menu.ItemGroup>
        ) || (
          null
        )}
      </Menu>
    );
  }

  AccountMenu() {

    return (
      <Menu className="user-layout-profile-dropdown-menu" onClick={this.handleClick.bind(this)}>
        <Menu.ItemGroup title="Profile">
          <Menu.Item key="/user-profile/account-settings">
            <LinearIcon type="user"/> My Profile
          </Menu.Item>
        </Menu.ItemGroup>
        <Menu.Divider/>
        <Menu.ItemGroup title="Organization">
          <Menu.Item key="/user-profile/organization-settings">
            <LinearIcon type="cog"/> Organization Settings
          </Menu.Item>
          <Menu.Item key="/user-profile/users">
            <LinearIcon type="users2"/> Users
          </Menu.Item>
          {/*<Menu.Item key="/user-profile/branding">*/}
          {/*Branding*/}
          {/*</Menu.Item>*/}
          <Menu.Item key="/user-profile/roles-and-permissions">
            <LinearIcon type="lock"/> Roles & Permissions
          </Menu.Item>
        </Menu.ItemGroup>

        {/*<Menu.Item key="/billing">*/}
        {/*Billing*/}
        {/*</Menu.Item>*/}
        <Menu.Divider className="user-layout--menu-divider"/>
        <Menu.Item key="logout">
          <LinearIcon type="exit"/> Log out
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
              <Dropdown overlayClassName={`user-layout--organization-select--overlay ${this.state.collapsed ? '' : 'user-layout--organization-select--overlay--open'}`} overlay={this.OrgSelection()} trigger={['hover']} placement="topLeft"
                        className="my-custom-dropdown">
                <Link to="/">
                  <img src={this.props.Organization.logoUrl} alt=""/>
                </Link>
              </Dropdown>
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
              { this.props.Organization && this.props.Organization.parentId === -1 ? (
                <Menu.Item key="/products">
                  <Icon type="appstore-o"/>
                  <span>Products</span>
                </Menu.Item>
              ) : (null)}
              {this.props.Organization && this.props.Organization.canCreateOrgs && (
                <Menu.Item key="/organizations">
                  <Icon type="usergroup-add"/>
                  <span>Organizations</span>
                </Menu.Item>
              )}
            </Menu>
            <div className="user-layout-left-navigation-profile">
              <Dropdown overlayClassName={`user-layout-left-navigation-profile--overlay ${this.state.collapsed ? '': 'user-layout-left-navigation-profile--overlay--open'}`} overlay={this.AccountMenu()} trigger={['hover']} placement="topLeft" className="my-custom-dropdown">
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
