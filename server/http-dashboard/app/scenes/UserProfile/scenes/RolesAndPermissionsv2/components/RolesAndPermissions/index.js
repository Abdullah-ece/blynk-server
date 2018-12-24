/*eslint-disable jsx-singlequotes*/
/*eslint-disable jsx-quotes*/

import React from 'react';
import { Table, Collapse, Switch } from 'antd';
import './styles.less';
import PropTypes from 'prop-types';
import LinearIcon from "components/LinearIcon";
import { PERMISSIONS_TABLE } from "services/Roles";

const Panel = Collapse.Panel;

class RolesAndPermissions extends React.Component {

  static propTypes = {
    roles: PropTypes.array,
    usersPerRole: PropTypes.object,
    GetPermissions: PropTypes.func,
    UpdateRole: PropTypes.func,
    GetRolesUsers: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.renderList = this.renderList.bind(this);
    this.buildHeaders = this.buildHeaders.bind(this);
    this.buildColumns = this.buildColumns.bind(this);
    this.buildDataSources = this.buildDataSources.bind(this);
    this.onPermissionChange = this.onPermissionChange.bind(this);
    this.buildGenericPermissionsPanel = this.buildGenericPermissionsPanel.bind(this);
    this.applyPermissions = this.applyPermissions.bind(this);
    this.addDataSource = this.addDataSource.bind(this);

    this.handleCollapseAll = this.handleCollapseAll.bind(this);
    this.handleExpandAll = this.handleExpandAll.bind(this);
    this.hanldeCollapseOnChange = this.hanldeCollapseOnChange.bind(this);

    this.state = {
      currentActiveKeys: ['1']
    };
  }

  componentDidMount() {
    this.props.GetPermissions().then(() => {
      this.props.GetRolesUsers();
    });
  }

  handleExpandAll() {
    this.setState({
      currentActiveKeys: ['1', '2', '3', '4', '5', '6']
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
              {this.buildGenericPermissionsPanel(1, 'Permissions Control', 17, 4, [18, 17, 19, 20])}
              {this.buildGenericPermissionsPanel(2, 'Users', 9, 4)}
              <Panel header={<div>
                <LinearIcon
                  type={this.state.currentActiveKeys.indexOf('3') < 0 ? "plus-square" : "minus-square"}/> Devices
              </div>} key={3} className='list-of-permissions-collapsed-panel'>
                <div className="list-of-permissions-items--content">
                  <div
                    className={'roles-list--role--table-border sub-header'}>Own
                    Devices
                  </div>
                  <Table showHeader={false}
                         pagination={false}
                         dataSource={this.buildDataSources(26, 5, [26, 27, 28, 30, 29])}
                         columns={this.buildColumns('permissions-table-main-column--level2')}/>
                  <div
                    className={'roles-list--role--table-border sub-header'}>Organization
                    Devices
                  </div>
                  <Table showHeader={false}
                         pagination={false}
                         dataSource={this.buildDataSources(21, 5, [21, 22, 23, 25, 24])}
                         columns={this.buildColumns('permissions-table-main-column--level2')}/>
                  <div
                    className={'roles-list--role--table-border sub-header'}>Auth
                    Token
                  </div>
                  <Table showHeader={false}
                         pagination={false}
                         dataSource={this.buildDataSources(31, 1)}
                         columns={this.buildColumns('permissions-table-main-column--level2')}/>
                </div>
              </Panel>
              {this.buildGenericPermissionsPanel(4, 'Blynk.Air: Firmware Over-The-Air Updates', 2, 3)}
              {this.buildGenericPermissionsPanel(5, 'Products', 13, 4)}
              <Panel header={<div>
                <LinearIcon
                  type={this.state.currentActiveKeys.indexOf('6') < 0 ? "plus-square" : "minus-square"}/> Organizations
              </div>} key={6} className='list-of-permissions-collapsed-panel'>
                <div className="list-of-permissions-items--content">
                  <Table showHeader={false}
                         pagination={false}
                         dataSource={this.buildDataSources(0, 2, [1, 0])}
                         columns={this.buildColumns('permissions-table-main-column--level2')}/>
                  <div
                    className={'roles-list--role--table-border sub-header'}>Sub
                    Organizations
                  </div>
                  <Table showHeader={false}
                         pagination={false}
                         dataSource={this.buildDataSources(5, 4, [6, 5, 7, 8])}
                         columns={this.buildColumns('permissions-table-main-column--level2')}/>
                </div>
              </Panel>
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
      permissionGroup2,
    } = role;


    this.props.UpdateRole({
      id,
      name,
      permissionGroup1: this.applyPermissions(role, index, value),
      permissionGroup2,
    });
  }

  applyPermissions(
    { permissionGroup1, permissionsGroup1Binary },
    index,
    value
  ) {
    const newPermissions = Math.pow(2, index);
    let result = permissionGroup1 ^ newPermissions;

    if (!value) {
      for (let perm of PERMISSIONS_TABLE[index].dependentPermsToRemove) {
        if (permissionsGroup1Binary[permissionsGroup1Binary.length - perm - 1] === '1') {
          result = result ^ Math.pow(2, perm);
        }
      }
    } else {
      for (let perm of PERMISSIONS_TABLE[index].dependentPermsToActivate) {
        if (permissionsGroup1Binary[permissionsGroup1Binary.length - perm - 1] === '0') {
          result = result ^ Math.pow(2, perm);
        }
      }
    }

    return result;
  }

  buildGenericPermissionsPanel(
    key,
    header,
    startingPermission,
    offset,
    permissionsList
  ) {
    return (
      <Panel header={<div>
        <LinearIcon
          type={this.state.currentActiveKeys.indexOf(key.toString()) < 0 ? "plus-square" : "minus-square"}/> {header}
      </div>} key={key} className='list-of-permissions-collapsed-panel'>
        <div className="list-of-permissions-items--content">
          <div className={'roles-list--role--table-border'}/>
          <Table showHeader={false}
                 pagination={false}
                 dataSource={this.buildDataSources(startingPermission, offset, permissionsList)}
                 columns={this.buildColumns('permissions-table-main-column')}/>
        </div>
      </Panel>
    );
  }

  buildDataSources(startingPermission, offset, permissionsList) {
    const result = [];

    if (!permissionsList) {
      for (let i = 0; i != offset; i++) {
        let currentIndex = startingPermission + i;
        this.addDataSource(result, currentIndex);
      }
    } else {
      for (let currentIndex of permissionsList) {
        this.addDataSource(result, currentIndex);
      }
    }

    return result;
  }

  addDataSource(result, currentIndex) {
    let value = {
      key: currentIndex,
      name: PERMISSIONS_TABLE[currentIndex].value,
    };

    for (let role of this.props.roles) {
      const { name, permissionsGroup1Binary } = role;

      value[name.toLowerCase().trim()] = {
        role,
        index: currentIndex,
        value: Boolean(Number(permissionsGroup1Binary[permissionsGroup1Binary.length - currentIndex - 1])),
      };
    }

    result.push(value);
  }

  buildColumns(className) {
    const result = [
      {
        dataIndex: 'name',
        key: 'name',
        width: '292px',
        style: { borderTop: '1px solid #e9e9e9;' },
        render: value => <div
          className={className}>{value}</div>,
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
            {this.props.usersPerRole[role.id] || 0} USERS
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
