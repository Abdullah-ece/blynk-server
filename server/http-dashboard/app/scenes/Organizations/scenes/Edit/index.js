import React                  from 'react';
import {connect}              from 'react-redux';
import {bindActionCreators}   from 'redux';
import {Roles}                from 'services/Roles';
import {
  message
}                             from 'antd';
import {
  Admins,
  Products
}                             from '../Details/components';
import {
  initialize,
  destroy,
  getFormSyncErrors,
  submit,
  getFormValues,
  registerField,
  SubmissionError,
  reset
}                             from 'redux-form';
import {Map, List, fromJS}    from 'immutable';
import PropTypes              from 'prop-types';
import {ProductsFetch}        from 'data/Product/api';
import {Manage}               from 'services/Organizations';

import {
  OrganizationsManageSetActiveTab,
  OrganizationsManageUpdate,
  OrganizationsCreate,
  OrganizationsFetch,
  OrganizationsUpdate,
  OrganizationsDetailsUpdate,
  OrganizationsManageUsersFetch
}                             from 'data/Organizations/actions';

import {
  OrganizationSendInvite,
  OrganizationUsersDelete
}                             from 'data/Organization/actions';


import {
  Edit as OrganizationEdit
}                             from 'scenes/Organizations/components';

@connect((state) => ({
  list: state.Organizations.get('list'),
  manage: state.Organizations.get('manage'),
  products: fromJS(state.Product.products),
  details: fromJS(state.Organizations.get('details')),
  activeTab: state.Organizations.getIn(['manage', 'activeTab']),
  formErrors: fromJS(getFormSyncErrors(Manage.FORM_NAME)(state) || {}),
  formValues: fromJS(getFormValues(Manage.FORM_NAME)(state) || {}),
}), (dispatch) => ({
  setTab: bindActionCreators(OrganizationsManageSetActiveTab, dispatch),
  resetForm: bindActionCreators(reset, dispatch),
  submitForm: bindActionCreators(submit, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
  updateManage: bindActionCreators(OrganizationsManageUpdate, dispatch),
  registerField: bindActionCreators(registerField, dispatch),
  fetchProducts: bindActionCreators(ProductsFetch, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  OrganizationsFetch: bindActionCreators(OrganizationsFetch, dispatch),
  OrganizationsUpdate: bindActionCreators(OrganizationsUpdate, dispatch),
  OrganizationsCreate: bindActionCreators(OrganizationsCreate, dispatch),
  OrganizationSendInvite: bindActionCreators(OrganizationSendInvite, dispatch),
  OrganizationUsersDelete: bindActionCreators(OrganizationUsersDelete, dispatch),
  OrganizationsDetailsUpdate: bindActionCreators(OrganizationsDetailsUpdate, dispatch),
  OrganizationsManageUsersFetch: bindActionCreators(OrganizationsManageUsersFetch, dispatch),
}))
class Edit extends React.Component {

  static contextTypes = {
    router: PropTypes.object
  };

  static propTypes = {
    setTab: PropTypes.func,
    resetForm: PropTypes.func,
    submitForm: PropTypes.func,
    destroyForm: PropTypes.func,
    updateManage: PropTypes.func,
    registerField: PropTypes.func,
    initializeForm: PropTypes.func,
    fetchProducts: PropTypes.func,
    OrganizationSave: PropTypes.func,
    OrganizationsFetch: PropTypes.func,
    OrganizationsCreate: PropTypes.func,
    OrganizationsUpdate: PropTypes.func,
    OrganizationSendInvite: PropTypes.func,
    OrganizationsManageUsersFetch: PropTypes.func,
    OrganizationUsersDelete: PropTypes.func,
    OrganizationsDetailsUpdate: PropTypes.func,

    params: PropTypes.object,

    details: PropTypes.instanceOf(Map),
    manage: PropTypes.instanceOf(Map),
    formErrors: PropTypes.instanceOf(Map),
    formValues: PropTypes.instanceOf(Map),

    list: PropTypes.instanceOf(List),
    products: PropTypes.instanceOf(List),

    activeTab: PropTypes.string
  };

  constructor(props) {
    super(props);

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleTabChange = this.handleTabChange.bind(this);
    this.handleSubmitFail = this.handleSubmitFail.bind(this);
    this.handleSubmitSuccess = this.handleSubmitSuccess.bind(this);

    this.handleAddAdmin = this.handleAddAdmin.bind(this);
    this.handleUsersDelete = this.handleUsersDelete.bind(this);
    this.handleUserInviteSuccess = this.handleUserInviteSuccess.bind(this);
  }

  componentWillMount() {

    const initializeForm = (data) => {
      this.props.initializeForm(Manage.FORM_NAME, {
        name: data.organization.get('name'),
        description: data.organization.get('description'),
        logoUrl: data.organization.get('logoUrl'),
        canCreateOrgs: data.organization.get('canCreateOrgs'),
        selectedProducts: (data.organization.get('products') || []).map(product => product.get('id')),
        admins: data.users.toJS()
      });

      this.props.registerField(Manage.FORM_NAME, 'admins', 'Field');
    };

    const loadOrganizations = () => {
      if (!this.props.list)
        return this.props.OrganizationsFetch();

      return new Promise((resolve) => resolve(this.props.list.toJS()));
    };

    const loadProducts = () => {
      if (!this.props.products)
        return this.props.fetchProducts();

      return new Promise((resolve) => resolve(this.props.products.toJS()));
    };

    const loadUsers = () => {
      if (!this.props.manage.getIn(['admins', 'list']))
        return this.props.OrganizationsManageUsersFetch({
          id: this.props.params.id
        });

      return new Promise((resolve) => resolve(this.props.manage.getIn(['admins', 'list']).toJS()));
    };

    this.props.updateManage(this.props.manage.set('loading', true));

    Promise.all([
      loadOrganizations(),
      loadUsers(),
      loadProducts(),
    ]).then(([list, users]) => {

      users = Array.isArray(users) ? users : users.payload.data;

      const organizations = Array.isArray(list) ? list : list.payload.data;

      const organization = organizations.find(org => org.id === Number(this.props.params.id));

      if (!organization)
        this.context.router.push('/organizations?notFound=true');

      this.props.updateManage(
        this.props.manage.set('organization', organization)
          .set('loading', false)
      );

      initializeForm({
        organization: fromJS(organization),
        users: fromJS(users)
      });
    });
  }

  componentWillUnmount() {
    this.props.updateManage(
      this.props.manage.set('activeTab', Manage.DEFAULT_TAB)
        .set('organization', null)
        .set('loading', false)
        .setIn(['admins', 'list'], null)
    );

    this.props.destroyForm(Manage.FORM_NAME);
  }

  TABS = {
    INFO: 'Info',
    PRODUCTS: 'Products',
    ADMINS: 'Admins'
  };

  handleCancel() {
    this.context.router.push(`/organizations/${this.props.params.id}`);
  }

  handleTabChange(tab) {
    this.props.setTab(tab);
  }

  handleSubmitSuccess() {
    this.context.router.push(`/organizations/${this.props.params.id}`);
  }

  handleSubmitFail() {
    //message.error('Cannot create organization');
  }

  handleSubmit() {

    const organization = this.props.list.find(org => {
      return org.get('id') === Number(this.props.params.id);
    });

    return new Promise((resolve) => {
      this.props.OrganizationsUpdate({
        ...organization.toJS(),
        ...this.props.formValues.toJS(),
        products: []
      }).then(() => {
        this.props.OrganizationsFetch().then(() => {
          resolve();
        });
      });
    });
  }

  handleUsersDelete(ids) {
    this.props.OrganizationsDetailsUpdate(this.props.details.set('userDeleteLoading', true));
    return new Promise((resolve) => {
      this.props.OrganizationUsersDelete(this.props.params.id, ids).then(() => {
        this.props.OrganizationsManageUsersFetch({
          id: this.props.params.id
        }).then(() => {
          this.props.OrganizationsDetailsUpdate(this.props.details.set('userDeleteLoading', false));
          resolve(true);
        });
      });
    });
  }

  handleAddAdmin(user) {
    this.props.OrganizationsDetailsUpdate(this.props.details.set('userInviteLoading', true));

    return (new Promise((resolve, reject) => {
      this.props.OrganizationSendInvite({
        id: this.props.params.id,
        email: user.email,
        name: user.name,
        role: Roles.ADMIN.value
      }).then(() => {
        this.props.OrganizationsManageUsersFetch({
          id: this.props.params.id
        }).then(() => {
          this.props.OrganizationsDetailsUpdate(this.props.details.set('userInviteLoading', false));

          resolve();
        });
      }).catch((response) => {
        const data = response.error.response.data;

        this.props.OrganizationsDetailsUpdate(this.props.details.set('userInviteLoading', false));

        reject(data);
      });
    })).catch((data) => {

      if (data && data.error && data.error.message) {
        throw new SubmissionError({'email': data.error.message});
      } else {
        message.error(data && data.error && data.error.message || 'Cannot invite user');
        throw new SubmissionError();
      }

    });
  }

  handleUserInviteSuccess() {
    message.success('Invite has been sent');
    this.props.resetForm(Manage.ADMIN_INVITE_FORM_NAME);
  }

  render() {

    if (this.props.manage.get('loading') || !this.props.manage.getIn(['admins', 'list']))
      return null;

    return (
      <OrganizationEdit
        formValues={this.props.formValues}
        formErrors={this.props.formErrors}
        form={Manage.FORM_NAME}
        onSubmit={this.handleSubmit}
        onSubmitSuccess={this.handleSubmitSuccess}
        onSubmitFail={this.handleSubmitFail}
        onCancel={this.handleCancel}
        edit={true} // hardcoded to hide admins tab
        products={this.props.products}
        onTabChange={this.handleTabChange}
        adminsComponent={<Admins
          onUsersDelete={this.handleUsersDelete}
          onUserAdd={this.handleAddAdmin}
          onUserInviteSuccess={this.handleUserInviteSuccess}
          userDeleteLoading={this.props.details.get('userDeleteLoading')}
          userInviteLoading={this.props.details.get('userInviteLoading')}
          users={this.props.manage.getIn(['admins', 'list']).filter(user => user.get('role') === Roles.ADMIN.value)}
        />}
        productsComponent={<Products products={this.props.products}/>}
        activeTab={this.props.activeTab}
      />
    );
  }

}

export default Edit;
