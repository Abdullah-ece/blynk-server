import React from 'react';

import {Table, Button, message, Popconfirm} from 'antd';

import {/*Status,*/ Role} from 'components/User';

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
      'usersDeleteLoading': false
    };
  }

  columns = [{
    title: 'Name',
    dataIndex: 'name',
    sorter: (a, b) => a.name > b.name,
  }, {
    title: 'Email',
    dataIndex: 'email',
    sorter: (a, b) => a.email > b.email,
  }, {
    title: 'Role',
    dataIndex: 'role',
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
    onFilter: (value, record) => record.role === value,
    sorter: (a, b) => a.role < b.role,
    render: (text, record) => <Role role={record.role} onChange={this.onRoleChange.bind(this, record)}/>
  },
    /*{
     title: 'Status',
     dataIndex: 'status',
     filters: [{
     text: 'Active',
     value: 1,
     }, {
     text: 'Pending',
     value: 0,
     }],
     filterMultiple: false,
     onFilter: (value, record) => Number(record.status) === Number(value),
     sorter: (a, b) => Number(a.status) < Number(b.status),
     render: (text, record) => <Status status={record.status}/>
     }*/
  ];

  handleDeleteUsers() {
    this.setState({
      usersDeleteLoading: true
    });

    this.props.OrganizationUsersDelete(this.props.Account.orgId, this.state.selectedRows).then(() => {
      this.props.OrganizationUsersFetch({id: this.props.Account.orgId}).then(() => {
        this.setState({
          usersDeleteLoading: false
        });
      });
    });
  }

  data = [];

  onRoleChange(user, role) {

    const hide = message.loading('Updating user role', 0);

    this.props.OrganizationUpdateUser(this.props.Account.orgId, Object.assign({}, user, {
      role: role.key
    })).then(() => {
      this.props.OrganizationUsersFetch({
        id: this.props.Account.orgId
      }).then(() => {
        hide();
      });
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

  render() {

    return (
      <div className="users-profile--organization-settings--organization-users">
        <div className="users-profile--organization-settings--organization-users-delete-button">
          <Popconfirm title="Are you sure you want to delete selected users?？"
                      okText="Yes"
                      cancelText="No"
                      onConfirm={this.handleDeleteUsers.bind(this)}>
            <Button type="danger"
                    disabled={!this.state.selectedRows}
                    loading={this.state.usersDeleteLoading}>Delete</Button>
          </Popconfirm>
        </div>
        <Table rowKey={(record) => record.email} rowSelection={this.rowSelection} columns={this.columns}
               dataSource={this.props.Organization.users}
               pagination={false}/>
      </div>
    );

  }

}

export default OrganizationUsers;
