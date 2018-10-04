import React                from 'react';
import {AdminInviteForm}    from './components';
import {alphabetSort}       from 'services/Sort';
import {ORG_INVITE_ROLE_ID} from 'services/Roles';
import {connect}            from 'react-redux';
import {Status}             from 'components/User';
import {
  Button,
  Table
}                           from 'antd';
import {
  fromJS,
  Map
}                           from 'immutable';
import {Manage}             from 'services/Organizations';
import {bindActionCreators} from 'redux';
import {
  change,
  getFormValues,
  SubmissionError,
  getFormSyncErrors,
  reset
}                           from 'redux-form';
import {
  OrganizationsCanInvite,
  OrganizationsManageUpdate
}                           from 'data/Organizations/actions';
import PropTypes            from 'prop-types';
import './styles.less';

@connect((state) => ({
  account: fromJS(state.Account),
  manage: fromJS(state.Organizations.get('manage')),
  formValues: fromJS(getFormValues(Manage.FORM_NAME)(state) || {}),
  formErrors: fromJS(getFormSyncErrors(Manage.FORM_NAME)(state) || {}),
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch),
  changeForm: bindActionCreators(change, dispatch),
  updateManage: bindActionCreators(OrganizationsManageUpdate, dispatch),
  OrganizationsCanInvite: bindActionCreators(OrganizationsCanInvite, dispatch),
  OrganizationsManageUpdate: bindActionCreators(OrganizationsManageUpdate, dispatch),
}))
class Admins extends React.Component {

  static propTypes = {
    resetForm: PropTypes.func,
    changeForm: PropTypes.func,
    updateManage: PropTypes.func,
    OrganizationsCanInvite: PropTypes.func,

    submitFailed: PropTypes.bool,

    manage: PropTypes.instanceOf(Map),
    account: PropTypes.instanceOf(Map),
    formErrors: PropTypes.instanceOf(Map),
    formValues: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.state = {
      selectedRows: 0,
      usersDeleteLoading: false,
      sortedInfo: {
        order: 'ascend',
        columnKey: 'name'
      }
    };

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleDeleteUser = this.handleDeleteUser.bind(this);
    this.handleTableChange = this.handleTableChange.bind(this);
    this.onRowSelectionChange = this.onRowSelectionChange.bind(this);
    this.handleSubmitSuccess = this.handleSubmitSuccess.bind(this);
  }

  handleSubmitSuccess() {
    this.props.resetForm(Manage.ADMIN_INVITE_FORM_NAME);
  }

  toggleCanInviteLoading(state) {
    this.props.updateManage(this.props.manage.setIn(['admins', 'canInviteLoading'], state));
  }

  handleSubmit(data) {

    const alreadyExists = this.props.formValues.get('admins').some((admin) => admin.email === data.email);

    if (alreadyExists) {
      throw new SubmissionError({
        'email': 'Email already exists'
      });
    }

    this.toggleCanInviteLoading(true);

    return this.props.OrganizationsCanInvite({
      orgId: this.props.account.get('orgId'),
      email: data.email
    }).then(() => {

      this.toggleCanInviteLoading(false);

      this.props.changeForm(Manage.FORM_NAME, 'admins', this.props.formValues.get('admins').update((admins) => admins.push({
        name: data.name,
        email: data.email,
        roleId: ORG_INVITE_ROLE_ID,
        status: 'Pending'
      })).toJS());

    }).catch((response) => {

      this.toggleCanInviteLoading(false);

      const data = response.error.response.data;

      throw new SubmissionError({
        'email': data && data.error && data.error.message
      });

    });
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

  rowSelection = {
    onChange: (selectedRowKeys) => this.onRowSelectionChange(selectedRowKeys)
  };

  handleDeleteUser() {

    this.props.changeForm(Manage.FORM_NAME, 'admins', this.props.formValues.get('admins').update(
      (admins) => admins.filter(admin => this.state.selectedRows.indexOf(admin.get('email')) === -1)).toJS()
    );
  }

  render() {

    const columns = this.updateColumns(this.state.sortedInfo);

    return (
      <div className="organizations">
        <div>Add at least one Administrator. Invitations will be sent out once you save the Organization.</div>
        <AdminInviteForm loading={this.props.manage.getIn(['admins', 'canInviteLoading'])} onSubmit={this.handleSubmit}
                         onSubmitSuccess={this.handleSubmitSuccess}/>

        { this.props.formErrors.get('admins') && this.props.submitFailed && (
          <div className="admin-invite-form--error">You should invite at least one administrator</div>
        )}


        <div>

          { !!this.props.formValues.get('admins').size && (
            <div className="admins-table-list">
              <div className="admins-table-list-delete-button">
                <Button type="danger"
                        disabled={!this.state.selectedRows.length}
                        onClick={this.handleDeleteUser}
                >Delete</Button>
              </div>
              <Table rowKey={(record) => record.email}
                     rowSelection={this.rowSelection}
                     columns={columns}
                     dataSource={this.props.formValues.get('admins').toJS()}
                     onChange={this.handleTableChange}
                     pagination={false}/>
            </div>
          )}

        </div>
      </div>
    );
  }

}

export default Admins;
