/*eslint-disable jsx-singlequotes*/
/*eslint-disable jsx-quotes*/

import React from 'react';
import {Table, Collapse, Switch } from 'antd';
import './styles.less';
import PropTypes from 'prop-types';
import LinearIcon from "../../../../../../components/LinearIcon";

const Panel = Collapse.Panel;

const PERMISSIONS_TABLE = [
  'ORG_SWITCH',
  'OWN_ORG_EDIT',
  'OTA_VIEW',
  'OTA_START',
  'OTA_STOP',

  'ORG_CREATE',
  'ORG_VIEW',
  'ORG_EDIT',
  'ORG_DELETE',

  'ORG_INVITE_USERS',
  'ORG_VIEW_USERS',
  'ORG_EDIT_USERS',
  'ORG_DELETE_USERS',

  'PRODUCT_CREATE',
  'PRODUCT_VIEW',
  'PRODUCT_EDIT',
  'PRODUCT_DELETE',

  'ROLE_CREATE',
  'ROLE_VIEW',
  'ROLE_EDIT',
  'ROLE_DELETE',

  'ORG_DEVICES_CREATE',
  'ORG_DEVICES_VIEW',
  'ORG_DEVICES_EDIT',
  'ORG_DEVICES_DELETE',

  'ORG_DEVICES_SHARE',
  'OWN_DEVICES_CREATE',
  'OWN_DEVICES_VIEW',
  'OWN_DEVICES_EDIT',
  'OWN_DEVICES_DELETE',
  'OWN_DEVICES_SHARE',

  'SET_AUTH_TOKEN',
];

class RolesAndPermissions extends React.Component {

  static propTypes = {
    roles: PropTypes.array,
    GetPermissions: PropTypes.func,
    UpdateRole: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.renderList = this.renderList.bind(this);
    this.buildHeaders = this.buildHeaders.bind(this);
    this.buildColumns = this.buildColumns.bind(this);
    this.buildDataSources = this.buildDataSources.bind(this);
    this.onPermissionChange = this.onPermissionChange.bind(this);
    this.buildPermissionsPanel = this.buildPermissionsPanel.bind(this);
    // this.handleAddRole = this.handleAddRole.bind(this);

    this.handleCollapseAll = this.handleCollapseAll.bind(this);
    this.handleExpandAll = this.handleExpandAll.bind(this);
    this.hanldeCollapseOnChange = this.hanldeCollapseOnChange.bind(this);

    this.state = {
      currentActiveKeys: ['1']
    };
  }

  componentDidMount() {
    this.props.GetPermissions();
  }

  handleExpandAll() {
    this.setState({
      currentActiveKeys: ['1', '2']
    });
  }

  handleCollapseAll() {
    this.setState({
      currentActiveKeys: ['']
    });
  }

  hanldeCollapseOnChange(key) {
    this.setState({
      currentActiveKeys: key
    });
  }

  renderList() {
    return (
      <div className="user-profile--roles-and-permissions">
        <div
          className="user-profile--roles-and-permissions--list-of-permissions">

          <div
            className={'user-profile--roles-and-permissions--list-of-permissions--header'}>
            <div
              className={'user-profile--roles-and-permissions--list-of-permissions--actions'}>
              Actions
              <div
                className={'user-profile--roles-and-permissions--list-of-permissions--actions-links'}>
                <a href='#' onClick={this.handleCollapseAll}>
                  Collapse all
                </a>
                <a href='#' onClick={this.handleExpandAll}>
                  Expand all
                </a>
              </div>
            </div>
            {this.buildHeaders()}
          </div>

          <div className="list-of-permissions-collapsed">
            <Collapse className="no-styles"
                      onChange={this.hanldeCollapseOnChange}
                      activeKey={this.state.currentActiveKeys}>
              {this.buildPermissionsPanel(1, 'OTA', 0, 5)}
              {this.buildPermissionsPanel(2, 'Organizations', 5, 4)}
              {this.buildPermissionsPanel(3, 'Users', 9, 4)}
              {this.buildPermissionsPanel(4, 'Products', 13, 4)}
              {this.buildPermissionsPanel(5, 'Roles', 17, 4)}
              {this.buildPermissionsPanel(6, 'Org Devices', 21, 5)}
              {this.buildPermissionsPanel(7, 'Own Devices', 26, 5)}
              {this.buildPermissionsPanel(8, 'Auth Token', 31, 1)}
            </Collapse>
          </div>
        </div>
      </div>
    );
  }

  onPermissionChange(role, index, value) {
    const {
      id,
      name,
      permissionsGroup1Binary,
      permissionGroup2,
    } = role;

    const newValue = permissionsGroup1Binary.split('');
    newValue[index] = Number(value).toString();

    this.props.UpdateRole({
      id,
      name,
      permissionGroup1: parseInt(newValue.join(''), 2) >> 0,
      permissionGroup2,
    });
  }

  buildPermissionsPanel(key, header, startingPermission, offset) {
    return (
      <Panel header={<div>
        <LinearIcon
          type={this.state.currentActiveKeys.indexOf(key.toString()) < 0 ? "plus-square" : "minus-square"}/> {header}
      </div>} key={key} className='list-of-permissions-collapsed-panel'>
        <div className="list-of-permissions-items--content">
          <Table className='roles-list--role--table' showHeader={false}
                 pagination={false}
                 dataSource={this.buildDataSources(startingPermission, offset)}
                 columns={this.buildColumns()} bordered/>
        </div>
      </Panel>
    );
  }

  buildDataSources(startingPermission, offset) {
    const result = [];

    for (let i = 0; i != offset; i++) {
      let currentIndex = startingPermission + i;
      let value = {
        key: currentIndex,
        name: PERMISSIONS_TABLE[currentIndex],
      };

      for (let role of this.props.roles) {
        value[role.name.toLowerCase().trim()] = {
          role,
          index: currentIndex,
          value: Boolean(Number(role.permissionsGroup1Binary[currentIndex])),
        };
      }

      result.push(value);
    }

    return result;
  }

  buildColumns() {
    const result = [
      {
        dataIndex: 'name',
        key: 'name',
        width: '292px',
        render: value => <div
          className='permissions-table-main-column'>{value}</div>,
      }
    ];

    for (let i = 0; i < this.props.roles.length; i++) {
      let role = this.props.roles[i];
      let value = {
        dataIndex: role.name.toLowerCase().trim(),
        key: role.name.toLowerCase().trim(),
        render: value => <Switch className='permissions-table-switch'
                                 size="small"
                                 checked={value.value} onChange={
          checked => this.onPermissionChange(value.role, value.index, checked)}/>,
      };

      if (i + 1 !== this.props.roles.length) {
        value.width = '184px';
      }

      result.push(value);
    }

    return result;
  }

  buildHeaders() {
    return this.props.roles.map((role) => {
        return (<div
          key={role.name}
          className={'user-profile--roles-and-permissions--list-of-permissions--role'}>
          <div
            className={'user-profile--roles-and-permissions--list-of-permissions--role-header'}>
            {role.name}
          </div>
          <div
            className={'user-profile--roles-and-permissions--list-of-permissions--users-count'}>
            0 USERS
          </div>
        </div>);
      }
    );
  }

  render() {

    return (
      this.renderList()
    );
  }

}

export default RolesAndPermissions;
