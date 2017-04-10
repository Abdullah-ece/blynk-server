import React from 'react';

import {Table, Button, Modal} from 'antd';

import {Status, Role} from 'components/User';

import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';

import {
  OrganizationFetch,
  OrganizationUsersFetch
} from 'data/Organization/actions';

import './styles.less';

@connect((state) => ({
  Organization: state.Organization,
  Account: state.Account,
}), (dispatch) => ({
  OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch),
  OrganizationUsersFetch: bindActionCreators(OrganizationUsersFetch, dispatch),
}))
class OrganizationUsers extends React.Component {

  static propTypes = {
    Account: React.PropTypes.object,
    Organization: React.PropTypes.object,
    OrganizationFetch: React.PropTypes.func,
    OrganizationUsersFetch: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.props.OrganizationUsersFetch({
      id: this.props.Account.orgId
    });

    this.state = {
      'selectedRows': 0
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
      text: 'Admin',
      value: 'admin',
    }, {
      text: 'Staff',
      value: 'staff',
    }],
    filterMultiple: false,
    onFilter: (value, record) => record.role === value,
    sorter: (a, b) => a.role < b.role,
    render: (text, record) => <Role role={record.role} onChange={this.onRoleChange.bind(this, record.id)}/>
  }, {
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
  }];

  data = [{
    key: '1',
    name: 'Albert Einstein',
    email: 'albert.einstein@gmail.com',
    role: 'admin',
    status: 0
  }, {
    key: '2',
    name: 'Nikola Tesla',
    email: 'nikola.tesla@tesla.com',
    role: 'staff',
    status: 1
  }, {
    key: '3',
    name: 'Frederic Chopin',
    email: 'frederic@warsaw.com',
    role: 'staff',
    status: 0
  }, {
    key: '4',
    name: 'Nicolaus Copernicus',
    email: 'superman@nicolaus.com',
    role: 'admin',
    status: 1
  }];

  onRoleChange(id, role) {
    console.log('role changed for ', id, role);
  }

  rowSelection = {
    onChange: this.onRowSelectionChange.bind(this)
  };

  onRowSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRows: selectedRows.length
    });
  }

  showDeleteConfirmationModal() {
    Modal.confirm({
      title: 'Warning',
      content: 'Are you sure you want to delete selected users?',
      okText: 'Ok',
      cancelText: 'Nope'
    });
  }

  render() {

    return (
      <div className="users-profile--organization-settings--organization-users">
        <div className="users-profile--organization-settings--organization-users-delete-button">
          <Button type="danger" onClick={this.showDeleteConfirmationModal.bind(this)}
                  disabled={!this.state.selectedRows}>Delete</Button>
        </div>
        <Table rowKey={(record) => record.id} rowSelection={this.rowSelection} columns={this.columns}
               dataSource={this.props.Organization.users}
               pagination={false}/>
      </div>
    );

  }

}

export default OrganizationUsers;
