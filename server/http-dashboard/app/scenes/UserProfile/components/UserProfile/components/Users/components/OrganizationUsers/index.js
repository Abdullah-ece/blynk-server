import React from 'react';
import {displayError} from "services/ErrorHandling";
import {alphabetSort} from 'services/Sort';
import {Table, Button, message, Popconfirm} from 'antd';

import {Status, Role} from 'components/User';

import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {Roles} from 'services/Roles';

import {
  OrganizationFetch,
  OrganizationUsersFetch,
  OrganizationUpdateUser,
  OrganizationUsersDelete
} from 'data/Organization/actions';

import './styles.less';

@connect((state) => ({
  Organization: state.Organization,
  Account: state.Account,
}), (dispatch) => ({
  OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch),
  OrganizationUsersFetch: bindActionCreators(OrganizationUsersFetch, dispatch),
  OrganizationUpdateUser: bindActionCreators(OrganizationUpdateUser, dispatch),
  OrganizationUsersDelete: bindActionCreators(OrganizationUsersDelete, dispatch),
}))
class OrganizationUsers extends React.Component {

  static propTypes = {
    Account: React.PropTypes.object,
    Organization: React.PropTypes.object,
    OrganizationFetch: React.PropTypes.func,
    OrganizationUsersFetch: React.PropTypes.func,
    OrganizationUpdateUser: React.PropTypes.func,
    OrganizationUsersDelete: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.props.OrganizationUsersFetch({
      id: this.props.Account.orgId
    });

    this.state = {
      'selectedRows': 0,
      'usersDeleteLoading': false,
      'sortedInfo': {
        order: 'ascend',
        columnKey: 'name'
      }
    };
  }

  updateColumns(sortedInfo) {
    return [{
      title: 'Name',
      dataIndex: 'name',
      sortOrder: sortedInfo.columnKey === 'name' && sortedInfo.order,
      sorter: (a, b) => alphabetSort(a.name, b.name),
      render: (text) => (<strong>{text}</strong>)
    }, {
      title: 'Email',
      dataIndex: 'email',
      sortOrder: sortedInfo.columnKey === 'email' && sortedInfo.order,
      sorter: (a, b) => alphabetSort(a.email, b.email),
    }, {
      title: 'Role',
      dataIndex: 'role',
      sortOrder: sortedInfo.columnKey === 'role' && sortedInfo.order,
      filters: [{
        text: Roles.ADMIN.title,
        value: Roles.ADMIN.value,
      }, {
        text: Roles.STAFF.title,
        value: Roles.STAFF.value,
      }, {
        text: Roles.USER.title,
        value: Roles.USER.value,
      }],
      filterMultiple: false,
      onFilter: (value, record) => record.roleId === value,
      render: (text, record) => <Role role={record.roleId} onChange={this.onRoleChange.bind(this, record)}/>
    }, {
      title: 'Status',
      dataIndex: 'status',
      sortOrder: sortedInfo.columnKey === 'status' && sortedInfo.order,
      filters: [{
        text: 'Active',
        value: 'Active',
      }, {
        text: 'Pending',
        value: 'Pending',
      }],
      filterMultiple: false,
      onFilter: (value, record) => record.status === value,
      sorter: (a, b) => alphabetSort(a.status, b.status),
      render: (text, record) => <Status status={record.status}/>
    }];
  }

  handleDeleteUsers() {
    this.setState({
      usersDeleteLoading: true
    });

    const initState = () => {
      this.setState({
        usersDeleteLoading: false,
        selectedRows: 0
      });
    };

    this.props.OrganizationUsersDelete(this.props.Account.orgId, this.state.selectedRows).then(() => {
      this.props.OrganizationUsersFetch({id: this.props.Account.orgId}).then(() => {
        initState();
      });
    }).catch((err) => {
      initState();
      displayError(err, message.error);
    });
  }

  data = [];

  onRoleChange(user, role) {

    const hide = message.loading('Updating user role', 0);
    const resetUsersList = () => {
      this.props.OrganizationUsersFetch({
        id: this.props.Account.orgId
      }).then(() => {
        hide();
      });
    };

    this.props.OrganizationUpdateUser(this.props.Account.orgId, Object.assign({}, user, {
      roleId: role
    })).then(() => {
      resetUsersList();
    }).catch((err) => {
      hide();
      resetUsersList();
      displayError(err, message.error);
    });
  }

  rowSelection = {
    onChange: this.onRowSelectionChange.bind(this)
  };

  onRowSelectionChange(selectedRowKeys) {
    this.setState({
      isAnyRowSelected: !!selectedRowKeys.length,
      selectedRows: selectedRowKeys
    });
  }

  handleTableChange(pagination, filters, sorter) {
    this.setState({
      sortedInfo: sorter,
    });
  }

  render() {

    const columns = this.updateColumns(this.state.sortedInfo);

    return (
      <div className="users-profile--organization-settings--organization-users">
        <div className="users-profile--organization-settings--organization-users-delete-button">
          <Popconfirm title="Are you sure you want to delete selected users?"
                      okText="Yes"
                      cancelText="No"
                      onConfirm={this.handleDeleteUsers.bind(this)}
                      overlayClassName="danger">
            <Button type="danger"
                    disabled={!this.state.selectedRows.length}
                    loading={this.state.usersDeleteLoading}>Delete</Button>
          </Popconfirm>
        </div>
        <Table rowKey={(record) => record.email} rowSelection={this.rowSelection} columns={columns}
               dataSource={this.props.Organization.users} onChange={this.handleTableChange.bind(this)}
               pagination={false}/>
      </div>
    );

  }

}

export default OrganizationUsers;
