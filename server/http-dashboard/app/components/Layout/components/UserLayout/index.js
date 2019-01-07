import {
  OrganizationFetch,
  OrganizationSwitch
} from "data/Organization/actions";
import {
  OrganizationsFetch,
  OrganizationsHierarchyFetch
} from "data/Organizations/actions";
import React from 'react';
import { Menu, Icon, Avatar, Dropdown } from 'antd';
import { LinearIcon } from "components";
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import * as AccountActions from 'data/Account/actions';
import {
  blynkWsConnect,
  blynkWsLogin
} from 'store/blynk-websocket-middleware/actions';
import { StartLoading, FinishLoading } from 'data/PageLoading/actions';
import { VerifyPermission, PERMISSIONS_INDEX, PERMISSIONS2_INDEX } from "services/Roles";

import './styles.less';
import Watermark from "../Watermark";

const DEFAULT_LOGO = '/static/logo.png';

@connect((state) => ({
  Account: state.Account,
  Organization: state.Organization,
  hierarchy: state.Organizations.get('hierarchy'),
  currentRole: state.RolesAndPermissions.currentRole,
}), (dispatch) => ({
  organizationsHierarchyFetch: bindActionCreators(OrganizationsHierarchyFetch, dispatch),
  organizationSwitch: bindActionCreators(OrganizationSwitch, dispatch),
  startLoading: bindActionCreators(StartLoading, dispatch),
  finishLoading: bindActionCreators(FinishLoading, dispatch),
  fetchAccount: bindActionCreators(AccountActions.Account, dispatch),
  selectOrgId: bindActionCreators(AccountActions.AccountSelectOrgId, dispatch),
  blynkWsConnect: bindActionCreators(blynkWsConnect, dispatch),
  blynkWsLogin: bindActionCreators(blynkWsLogin, dispatch),
  OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch),
  OrganizationsFetch: bindActionCreators(OrganizationsFetch, dispatch)
}))
class UserLayout extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    Account: React.PropTypes.object,
    children: React.PropTypes.object,
    location: React.PropTypes.object,
    fetchAccount: React.PropTypes.func,
    blynkWsConnect: React.PropTypes.func,
    selectOrgId: React.PropTypes.func,
    blynkWsLogin: React.PropTypes.func,
    Organization: React.PropTypes.object,
    hierarchy: React.PropTypes.object,
    startLoading: React.PropTypes.func,
    finishLoading: React.PropTypes.func,
    OrganizationFetch: React.PropTypes.func,
    OrganizationsFetch: React.PropTypes.func,
    organizationsHierarchyFetch: React.PropTypes.func,
    organizationSwitch: React.PropTypes.func,
    currentRole: React.PropTypes.object,
  };

  constructor(props) {
    super(props);

    this.state = {
      collapsed: true,
      current: props.location.pathname,
      logoUrl: props.Organization.logoUrl || DEFAULT_LOGO
    };

    this.fetchData();

    props.fetchAccount();

    props.organizationsHierarchyFetch();

    this.OrgSelection = this.OrgSelection.bind(this);
    this.handleOrgSelect = this.handleOrgSelect.bind(this);
    this.handleMouseEnter = this.handleMouseEnter.bind(this);
    this.handleMouseLeave = this.handleMouseLeave.bind(this);
    this.onImageError = this.onImageError.bind(this);
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
      current: props.location.pathname,
      logoUrl: props.Organization.logoUrl || DEFAULT_LOGO
    });
  }

  componentDidUpdate(prevProps) {
    if (prevProps.Account.selectedOrgId !== this.props.Account.selectedOrgId) {
      this.fetchData();
      this.context.router.push('/devices');
    }
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

  onImageError() {
    this.setState({ logoUrl: DEFAULT_LOGO });
  }

  handleOrgSelect(e) {
    this.props.organizationSwitch({
      orgId: e.key
    });

    this.props.selectOrgId({
      orgId: e.key
    });
  }

  currentActivePage(state) {
    const splitedPath = state.split('/');

    if ('products' === splitedPath[1] || 'product' === splitedPath[1])
      return ['/products'];

    if ('devices' === splitedPath[1])
      return ['/devices'];

    if ('organizations' === splitedPath[1])
      return ['/organizations'];

    if (process.env.BLYNK_ANALYTICS && JSON.parse(process.env.BLYNK_ANALYTICS) && 'analytics' === splitedPath[1])
      return ['/analytics'];

    if ('rules' === splitedPath[1]) {
      return ['/rules'];
    }

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
      if (!child)
        return null;

      let children = [];

      if (child && child.get('childs') && child.get('childs').map) {
        child.get('childs').map((child) => {
          children.push(renderChild(child, level + 1));
        });
      }

      return [
        <Menu.Item key={(child.get('id')).toString()}
                   className={`user-layout--organization-select--org-level-${level} ${isActive(child.get('id'))}`}>
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
      if (Number(id) === Number(currentOrgId)) {
        return 'user-layout--organization-select--org-active';
      }
      return '';
    };

    return (
      <Menu className="user-layout--organization-select"
            onClick={this.handleOrgSelect.bind(this)}>
        <Menu.ItemGroup title="Main Organization"
                        className={`user-layout--organization-select--meta-org`}>
          <Menu.Item key={`${id}`}>
            {name}
          </Menu.Item>
        </Menu.ItemGroup>
        <Menu.Divider/>
        {childs && childs.size && (
          <Menu.ItemGroup title="Sub Organizations">
            {childs.map((child) => renderChild(child))}
          </Menu.ItemGroup>
        ) || (
          null
        )}
      </Menu>
    );
  }

  AccountMenu() {

    return (
      <Menu className="user-layout-profile-dropdown-menu"
            onClick={this.handleClick.bind(this)}>
        <Menu.ItemGroup title="Profile">
          <Menu.Item key="/user-profile/account-settings">
            <LinearIcon type="user"/> My Profile
          </Menu.Item>
        </Menu.ItemGroup>
        <Menu.Divider/>
        <Menu.ItemGroup title="Organization">
          {VerifyPermission(this.props.currentRole.permissionGroup1, PERMISSIONS_INDEX.ORG_VIEW) && (<Menu.Item key="/user-profile/organization-settings">
            <LinearIcon type="cog"/> Organization Settings
          </Menu.Item>)}
          {VerifyPermission(this.props.currentRole.permissionGroup1, PERMISSIONS_INDEX.ORG_VIEW_USERS) && (<Menu.Item key="/user-profile/users">
            <LinearIcon type="users2"/> Users
          </Menu.Item>)}
          {/*<Menu.Item key="/user-profile/branding">*/}
          {/*Branding*/}
          {/*</Menu.Item>*/}
          {VerifyPermission(this.props.currentRole.permissionGroup1, PERMISSIONS_INDEX.ROLE_VIEW) && (<Menu.Item key="/user-profile/roles-and-permissions">
            <LinearIcon type="lock"/> Roles & Permissions
          </Menu.Item>)}
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
            className={`user-layout-left-navigation ${this.state.navigationActive ? 'user-layout-left-navigation-active' : ''} ${this.state.collapsed ? 'user-layout-left-navigation-fold' : 'user-layout-left-navigation-unfold'}`}
            onMouseOver={this.handleMouseEnter}
            onMouseOut={this.handleMouseLeave}
          >
            <div
              className={this.state.collapsed ? 'user-layout-left-navigation-fold-company-logo' : 'user-layout-left-navigation-unfold-company-logo'}>
              {VerifyPermission(this.props.currentRole.permissionGroup1, PERMISSIONS_INDEX.ORG_SWITCH) ? (
                  <Dropdown
                    overlayClassName={`user-layout--organization-select--overlay ${this.state.collapsed ? '' : 'user-layout--organization-select--overlay--open'}`}
                    overlay={this.OrgSelection()} trigger={['hover']}
                    placement="topLeft"
                    className="my-custom-dropdown">
                    <Link to="/">
                      <img src={this.state.logoUrl} onError={this.onImageError}
                           alt=""/>
                    </Link>
                  </Dropdown>) :
                (<Link to="/">
                  <img src={this.state.logoUrl} onError={this.onImageError}
                       alt=""/>
                </Link>)}
            </div>
            <div className={`user-layout-left-navigation-collapse-btn`}>
              <Icon type={this.state.collapsed ? 'menu-unfold' : 'menu-fold'}
                    onClick={this.toggleCollapsed}/>
            </div>
            <Menu
              onClick={this.handleClick.bind(this)}
              className={`user-layout-left-navigation-menu`}
              mode="inline"
              inlineCollapsed={this.state.collapsed}
              selectedKeys={this.currentActivePage(this.state.current)}
            >
              {process.env.BLYNK_ANALYTICS && JSON.parse(process.env.BLYNK_ANALYTICS) &&
              <Menu.Item key="/analytics">
                <Icon type="bar-chart"/>
                <span>Analytics</span>
              </Menu.Item>}
              <Menu.Item key="/devices">
                <Icon type="hdd"/>
                <span>Devices</span>
              </Menu.Item>
              {this.props.Organization && this.props.Organization.parentId === -1 ? (
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
              {VerifyPermission(this.props.currentRole.permissionGroup2, PERMISSIONS2_INDEX.RULE_GROUP_VIEW) && (
                <Menu.Item key="/rules">
                  <Icon type="setting"/>
                  <span>Rules Engine</span>
                </Menu.Item>
              )}
            </Menu>
            <div className="user-layout-left-navigation-profile">
              <Dropdown
                overlayClassName={`user-layout-left-navigation-profile--overlay ${this.state.collapsed ? '' : 'user-layout-left-navigation-profile--overlay--open'}`}
                overlay={this.AccountMenu()} trigger={['hover']}
                placement="topLeft" className="my-custom-dropdown">
                <Avatar size="large" icon="user"
                        className="user-layout-left-navigation-profile-button"/>
              </Dropdown>
              <div>
                {!this.state.collapsed && this.props.Account.name}
              </div>
            </div>
          </div>
        </div>
        <div
          className={`user-layout-right-content ${this.state.collapsed ? 'user-layout-right-content-fold' : 'user-layout-right-content-unfold'}`}>
          {this.props.children}
        </div>
        <Watermark/>
      </div>
    );
  }

}

export default UserLayout;
