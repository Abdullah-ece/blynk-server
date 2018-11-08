import React                  from 'react';
import {connect}              from 'react-redux';
import {bindActionCreators}   from 'redux';
import {
  message
}                           from 'antd';
import {
  initialize,
  destroy,
  getFormSyncErrors,
  getFormAsyncErrors,
  getFormSubmitErrors,
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
  OrganizationsDelete,
  OrganizationsUpdate,
  OrganizationsUsersFetch,
  OrganizationsDetailsUpdate,
}                             from 'data/Organizations/actions';

import {
  OrganizationSendInvite,
  OrganizationUsersDelete,
}                             from 'data/Organization/actions';


import {
  Edit as OrganizationEdit
}                             from 'scenes/Organizations/components';
import {
  ProductsEdit
}                             from 'scenes/Organizations/scenes';

import AdminsEditScene from "../AdminsEdit/index";

@connect((state) => ({
  list: state.Organizations.get('list'),
  admins: state.Organizations.get('adminsEdit'),
  manage: state.Organizations.get('manage'),
  products: fromJS(state.Product.products),
  details: fromJS(state.Organizations.get('details')),
  activeTab: state.Organizations.getIn(['manage', 'activeTab']),
  formErrors: fromJS(getFormSyncErrors(Manage.FORM_NAME)(state) || {}),
  formAsyncErrors: fromJS(getFormAsyncErrors(Manage.FORM_NAME)(state) || {}),
  formSubmitErrors: fromJS(getFormSubmitErrors(Manage.FORM_NAME)(state) || {}),
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
  OrganizationsDelete: bindActionCreators(OrganizationsDelete, dispatch),
  OrganizationsUpdate: bindActionCreators(OrganizationsUpdate, dispatch),
  OrganizationsCreate: bindActionCreators(OrganizationsCreate, dispatch),
  OrganizationSendInvite: bindActionCreators(OrganizationSendInvite, dispatch),
  OrganizationsUsersFetch: bindActionCreators(OrganizationsUsersFetch, dispatch),
  OrganizationUsersDelete: bindActionCreators(OrganizationUsersDelete, dispatch),
  OrganizationsDetailsUpdate: bindActionCreators(OrganizationsDetailsUpdate, dispatch),
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
    OrganizationsDelete: PropTypes.func,
    OrganizationsCreate: PropTypes.func,
    OrganizationsUpdate: PropTypes.func,
    OrganizationSendInvite: PropTypes.func,
    OrganizationsUsersFetch: PropTypes.func,
    OrganizationUsersDelete: PropTypes.func,
    OrganizationsDetailsUpdate: PropTypes.func,

    params: PropTypes.object,

    admins: PropTypes.instanceOf(Map),
    manage: PropTypes.instanceOf(Map),
    details: PropTypes.instanceOf(Map),
    formErrors: PropTypes.instanceOf(Map),
    formValues: PropTypes.instanceOf(Map),
    formAsyncErrors: PropTypes.instanceOf(Map),
    formSubmitErrors: PropTypes.instanceOf(Map),

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
    this.handleOrganizationDelete = this.handleOrganizationDelete.bind(this);
  }

  componentWillMount() {

    const initializeForm = (data) => {
      this.props.initializeForm(Manage.FORM_NAME, {
        name: data.organization.get('name'),
        description: data.organization.get('description'),
        logoUrl: data.organization.get('logoUrl'),
        canCreateOrgs: data.organization.get('canCreateOrgs'),
        selectedProducts: (data.organization.get('selectedProducts') || []),
        products: (data.organization.get('products') || fromJS([])),
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
      if (!this.props.admins.get('users'))
        return this.props.OrganizationsUsersFetch({
          id: this.props.params.id
        });

      return new Promise((resolve) => resolve(this.props.admins.get('users').toJS()));
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
     const activeTab =  Object.values(this.TABS).indexOf(this.props.params.tab) > -1 ?
                                                              this.props.params.tab : this.TABS.INFO;
    this.props.setTab(activeTab);
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
    INFO: 'info',
    PRODUCTS: 'products',
    ADMINS: 'admins'
  };

  handleCancel(tab) {
    this.context.router.push(`/organizations/${this.props.params.id}/${tab}`);
  }

  handleTabChange(tab) {
    this.props.setTab(tab);
    this.context.router.push(`/organizations/edit/${this.props.params.id}/${tab}`);
  }

  handleSubmitSuccess() {
     this.context.router.push(`/organizations/${this.props.params.id}/${this.props.params.tab}`);
  }

  handleSubmitFail() {
    //message.error('Cannot create organization');
  }

  handleSubmit() {

    const organization = this.props.list.find(org => {
      return org.get('id') === Number(this.props.params.id);
    });

    return (new Promise((resolve, reject) => {
      this.props.OrganizationsUpdate({
        ...organization.toJS(),
        ...this.props.formValues.toJS(),
      }).then(() => {
        this.props.OrganizationsFetch().then(() => {
          resolve();
        });
      }).catch((err) => {
        reject({orgUpdate: err});
      });
    })).catch((err) => {
      if (err.orgUpdate) {
        throw new SubmissionError({
          name: err.orgUpdate.error.response.data.error.message
        });
      }
    });
  }

  toggleOrganizationDeleteLoading(state) {
    this.props.OrganizationsDetailsUpdate(
      this.props.details.set('organizationDeleteLoading', state)
    );
  }

  handleOrganizationDelete() {
    this.toggleOrganizationDeleteLoading(true);

    this.props.OrganizationsDelete({
      id: this.props.params.id
    }).then(() => {

      this.props.OrganizationsFetch().then(() => {

        this.context.router.push('/organizations');

        this.toggleOrganizationDeleteLoading(false);
      });

    }).catch((response) => {
      this.toggleOrganizationDeleteLoading(false);

      const data = response.error.response.data;

      message.error(data.error && data.error.message || 'Cannot delete organization');
    });
  }

  render() {

    if (this.props.manage.get('loading') || !this.props.admins.get('users'))
      return null;

    return (
      <OrganizationEdit
        formErrors={this.props.formErrors}
        formAsyncErrors={this.props.formAsyncErrors}
        formSubmitErrors={this.props.formSubmitErrors}
        formValues={this.props.formValues}
        form={Manage.FORM_NAME}
        onSubmit={this.handleSubmit}
        onSubmitSuccess={this.handleSubmitSuccess}
        onSubmitFail={this.handleSubmitFail}
        onCancel={this.handleCancel}
        onDelete={this.handleOrganizationDelete}
        products={this.props.products}
        onTabChange={this.handleTabChange}
        adminsComponent={<AdminsEditScene params={this.props.params} allowResendInvite={true} orgId={Number(this.props.params.id)}/>}
        productsComponent={<ProductsEdit products={this.props.products}/>}
        activeTab={this.props.activeTab}
      />
    );
  }

}

export default Edit;
