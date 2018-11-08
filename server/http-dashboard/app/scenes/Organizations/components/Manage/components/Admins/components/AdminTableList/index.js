import React                from 'react';
import {alphabetSort}       from 'services/Sort';
import {Status}             from 'components/User';
import PropTypes            from 'prop-types';
import {
  Button,
  Table,
  Popconfirm
}                           from 'antd';
import {connect}            from 'react-redux';
import {bindActionCreators} from 'redux';
import {
  OrganizationsAdminTableListUpdateSelectedRows,
  OrganizationsAdminTableListUpdateSortInfo,
}                           from 'data/Organizations/actions';
import {
  List,
  Map
}                           from 'immutable';
import './styles.less';
import {ResendInvite} from "components";

@connect((state) => ({
  sortInfo: state.Organizations.getIn(['adminTableListOptions', 'sortInfo']),
  selectedRows: state.Organizations.getIn(['adminTableListOptions', 'selectedRows'])
}), (dispatch) => ({
  updateSortInfo: bindActionCreators(OrganizationsAdminTableListUpdateSortInfo, dispatch),
  updateSelectedRows: bindActionCreators(OrganizationsAdminTableListUpdateSelectedRows, dispatch),
}))
class AdminTableList extends React.Component {

  static propTypes = {
    data: PropTypes.instanceOf(List),
    sortInfo: PropTypes.instanceOf(Map),
    selectedRows: PropTypes.instanceOf(List),

    allowResendInvite: PropTypes.bool,

    onAdminDelete: PropTypes.func,
    updateSortInfo: PropTypes.func,
    updateSelectedRows: PropTypes.func,

    orgId: PropTypes.number,

    loading: PropTypes.bool,
  };

  constructor(props) {
    super(props);

    this.handleDeleteUser = this.handleDeleteUser.bind(this);
    this.handleTableOptionsChange = this.handleTableOptionsChange.bind(this);
    this.handleRowSelectionChange = this.handleRowSelectionChange.bind(this);
  }

  componentWillUnmount() {
    this.props.updateSortInfo({
      order: null,
      columnKey: null
    });

    this.props.updateSelectedRows([]);
  }

  getColumnsBySortInfo(sortInfo) {
    return [{
      title: 'Name',
      dataIndex: 'name',
      sortOrder: sortInfo.columnKey === 'name' && sortInfo.order,
      sorter: (a, b) => alphabetSort(a.name, b.name),
      render: (text) => (<strong>{text}</strong>)
    }, {
      title: 'Email',
      dataIndex: 'email',
      sortOrder: sortInfo.columnKey === 'email' && sortInfo.order,
      sorter: (a, b) => alphabetSort(a.email, b.email),
    }, {
      title: 'Status',
      dataIndex: 'status',
      sortOrder: sortInfo.columnKey === 'status' && sortInfo.order,
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
    }, {
      title: '',
      dataIndex: '',
      render: (text, record) => {
        if(this.props.allowResendInvite !== true)
          return null;

        if(record.status === 'Pending') {

          const user = {
            orgId: this.props.orgId,
            name: record.name,
            email: record.email,
            roleId: record.roleId,
          };

          return (
            <ResendInvite user={user}/>
          );
        }
      }
    }];
  }

  handleDeleteUser() {

    const result = this.props.onAdminDelete(this.props.selectedRows);

    if (typeof result.then === 'function') {
      result.then(() => {
        this.props.updateSelectedRows([]);
      });
    }

    if (result === true) {
      this.props.updateSelectedRows([]);
    }

  }

  handleTableOptionsChange(pagination, filters, sorter) {
    this.props.updateSortInfo({
      order: sorter.order,
      columnKey: sorter.columnKey
    });
  }

  handleRowSelectionChange(selectedRowKeys) {
    this.props.updateSelectedRows(selectedRowKeys);
  }

  render() {
    const columns = this.getColumnsBySortInfo(this.props.sortInfo.toJS());

    return (
      <div className="admins-table-list">
        <div className="admins-table-list-delete-button">
          <Popconfirm title="Are you sure you want to delete selected users?"
                      okText="Yes"
                      cancelText="No"
                      onConfirm={this.handleDeleteUser}
                      overlayClassName="danger">
            <Button type="danger"
                    loading={this.props.loading}
                    disabled={!this.props.selectedRows.size || this.props.loading}
            >Delete</Button>
          </Popconfirm>
        </div>
        <Table locale={{emptyText: 'No Admins'}}
               rowKey={(record) => record.email}
               columns={columns}
               pagination={false}
               dataSource={this.props.data.toJS()}
               onChange={this.handleTableOptionsChange}
               rowSelection={{onChange: this.handleRowSelectionChange, selectedRowKeys: this.props.selectedRows.toJS()}}
        />
      </div>
    );
  }

}

export default AdminTableList;
