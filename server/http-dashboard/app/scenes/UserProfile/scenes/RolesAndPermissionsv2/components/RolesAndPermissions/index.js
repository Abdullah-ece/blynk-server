import React from 'react';
import {/*Button,*/ Table, /*Checkbox,*/ Collapse, Switch } from 'antd';
// import { SimpleContentEditable } from 'components';
// import PropTypes from 'prop-types';
// import Scroll from "react-scroll";
import './styles.less';
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

  static propTypes = {};

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

  // componentDidUpdate(prevProps) {
  //
  //   if (prevProps && prevProps.formValues && prevProps.formValues.roles) {
  //     let newId = null;
  //
  //     const lastIds = prevProps.formValues.roles.map((role) => {
  //       return Number(role.id);
  //     });
  //
  //     this.props.formValues.roles.forEach((role) => {
  //       if (lastIds.indexOf(Number(role.id)) === -1) {
  //         newId = role.id;
  //       }
  //     });
  //
  //     if (newId) {
  //
  //       Scroll.scroller.scrollTo(`role-${newId}`, {
  //         duration: 1000,
  //         offset: -32,
  //         smooth: "easeInOutQuint",
  //       });
  //     }
  //   }
  //
  // }

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

  // roleNameComponent({ input }) {
  //   return (
  //     <SimpleContentEditable maxLength={35}
  //                            className="user-profile--roles-and-permissions--roles-list--role--title"
  //                            value={input.value}
  //                            onChange={input.onChange}/>
  //   );
  // }

  // handleAddRole() {
  //   if (typeof this.props.onAddRole === 'function')
  //     this.props.onAddRole();
  // }

  // handleSortStart({index, node, collection}, event) {
  //
  // }

  // permissionCheckbox({ input, permissionType }) {
  //
  //   const getValue = () => {
  //     return (input.value & permissionType.mask) === permissionType.mask;
  //   };
  //
  //   const onChange = (event) => {
  //
  //     const checked = event.target.checked;
  //
  //     if ((input.value & permissionType.mask) && !checked) {
  //       input.onChange(input.value - permissionType.mask);
  //     } else if (!(input.value & permissionType.mask) && checked) {
  //       input.onChange(input.value + permissionType.mask);
  //     }
  //   };
  //
  //   const value = !!getValue();
  //
  //   return (<Checkbox checked={value} onChange={onChange}/>);
  // }

  renderList(/*{fields}*/) {
    // const prepareData = (field) => {
    //   return ([{
    //     key   : '1',
    //     name  : 'Devices',
    //     view  : {
    //       field: field,
    //       type: 'devices'
    //     },
    //     edit  : {
    //       field: field,
    //       type: 'devices'
    //     },
    //     remove: {
    //       field: field,
    //       type: 'devices'
    //     },
    //   }, {
    //     key   : '2',
    //     name  : 'Products',
    //     view  : {
    //       field: field,
    //       type: 'products'
    //     },
    //     edit  : {
    //       field: field,
    //       type: 'products'
    //     },
    //     remove: {
    //       field: field,
    //       type: 'products'
    //     },
    //   }, {
    //     key   : '3',
    //     name  : 'Sub Organizations',
    //     view  : {
    //       field: field,
    //       type: 'suborganizations'
    //     },
    //     edit  : {
    //       field: field,
    //       type: 'suborganizations'
    //     },
    //     remove: {
    //       field: field,
    //       type: 'suborganizations'
    //     },
    //   }, {
    //     key   : '4',
    //     name  : 'Organization',
    //     view  : {
    //       field: field,
    //       type: 'organization'
    //     },
    //     edit  : {
    //       field: field,
    //       type: 'organization'
    //     },
    //     remove: {
    //       field: field,
    //       type: 'organization'
    //     },
    //   }]);
    // };

    // const columns = [
    //   {
    //   // title    : (<p className="user-profile--roles-and-permissions--roles-list--role--table-column-title-clickable" onClick={() => ({})}>Permission name</p>),
    //   title    : (<p>Permission name</p>),
    //   dataIndex: 'name',
    //   // render   : (value) => (<p className="user-profile--roles-and-permissions--roles-list--role--table-name-clickable" onClick={() => ({})}>{value}</p>)
    //   render   : (value) => (<p>{value}</p>)
    // }, {
    //   // title    : (<p className="user-profile--roles-and-permissions--roles-list--role--table-column-title-clickable" onClick={() => ({})}>View</p>),
    //   title    : (<p>View</p>),
    //   dataIndex: 'view',
    //   render   : ({field, type}) => {
    //     return (<Field component={this.permissionCheckbox} name={`${field}.${type}`}
    //                    permissionType={PERMISSION_TYPES.VIEW}/>);
    //   }
    // }, {
    //   // title    : (<p className="user-profile--roles-and-permissions--roles-list--role--table-column-title-clickable" onClick={() => ({})}>Edit</p>),
    //   title    : (<p>Edit</p>),
    //   dataIndex: 'edit',
    //   render   : ({field, type}) => {
    //     return (<Field component={this.permissionCheckbox} name={`${field}.${type}`}
    //                    permissionType={PERMISSION_TYPES.EDIT}/>);
    //   }
    // }, {
    //   // title    : (<p className="user-profile--roles-and-permissions--roles-list--role--table-column-title-clickable" onClick={() => ({})}>Delete</p>),
    //   title    : (<p>Delete</p>),
    //   dataIndex: 'remove',
    //   render   : ({field, type}) => {
    //     return (<Field component={this.permissionCheckbox} name={`${field}.${type}`}
    //                    permissionType={PERMISSION_TYPES.DELETE}/>);
    //   }
    // }];

    // const handleAddRole = () => {
    //   fields.push({
    //     id: new Date().getTime(),
    //     name: 'Role',
    //     devices         : 111,
    //     products        : 111,
    //     suborganizations: 111,
    //     organization    : 111,
    //   });
    // };

    // const handleSortEnd = ({oldIndex, newIndex}) => {
    //   fields.move(oldIndex, newIndex);
    // };

    // const handleRemove = (index) => {
    //   fields.remove(index);
    // };
    //
    // const handleCopy = (index) => {
    //   let field = fields.get(index);
    //
    //   let name = `${field.name} Copy`;
    //
    //   fields.insert(index + 1,
    //     {
    //     ...field,
    //     name: name,
    //     id: new Date().getTime()
    //   });
    // };

    const dataSource = [{
      key: '1',
      name: 'Create',
      admin: true,
      staff: true
    }, {
      key: '2',
      name: 'View',
      admin: true,
      staff: false
    }, {
      key: '3',
      name: 'Edit',
      admin: true,
      staff: false
    }, {
      key: '4',
      name: 'Delete',
      admin: true,
      staff: false
    }];

    const columns = [{
      dataIndex: 'name',
      key: 'name',
      width: '292px',
      render: value => <div
        className='permissions-table-main-column'>{value}</div>,
    }, {
      dataIndex: 'admin',
      key: 'admin',
      width: '184px',
      render: value => <Switch className='permissions-table-switch' size="small"
                               checked={value}/>,
    }, {
      dataIndex: 'staff',
      key: 'staff',
      render: value => <Switch className='permissions-table-switch' size="small"
                               checked={value}/>,
    }];
    
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
              <Panel header={<div>
                <LinearIcon
                  type={this.state.currentActiveKeys.indexOf('1') < 0 ? "plus-square" : "minus-square"}/> Devices
              </div>} key={1} className='list-of-permissions-collapsed-panel'>
                <div className="list-of-permissions-items--content">
                  <Table className='roles-list--role--table' showHeader={false}
                         pagination={false} dataSource={dataSource}
                         columns={columns} bordered/>
                </div>
              </Panel>
              {this.buildPermissionsPanel(2,'Organization', 0, 4)}
            </Collapse>
          </div>
        </div>
      </div>
    );
  }

  onPermissionChange(role, index, value) {
    console.log(role, index, value);
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
          role: role.name.toLowerCase().trim(),
          index: currentIndex,
          value: Boolean(Number(role.permissionsGroup1Binary[currentIndex])),
        };
      }

      result.push(value);
    }
    console.log(result);
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

    for (let role of this.props.roles) {
      let value = {
        dataIndex: role.name.toLowerCase().trim(),
        key: role.name.toLowerCase().trim(),
        width: '184px',
        render: value => <Switch className='permissions-table-switch'
                                 size="small"
                                 checked={value.value} onChange={
          checked => this.onPermissionChange(value.role, value.index, checked)}/>,
      };


      result.push(value);
    }
    console.log(result);
    return result;
  }

  buildHeaders() {
    return this.props.roles.map((role) => {
        return (<div
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
