import React                  from 'react';
import {connect}              from 'react-redux';
import {bindActionCreators}   from 'redux';
import {
  initialize,
  destroy,
  getFormSyncErrors,
  submit,
  getFormValues,
  registerField
}              from 'redux-form';
import {Map, List, fromJS}    from 'immutable';
import PropTypes              from 'prop-types';
import {ProductsFetch}        from 'data/Product/api';
import {Manage}               from 'services/Organizations';

import {
  OrganizationsManageSetActiveTab,
  OrganizationsManageUpdate,
  OrganizationsCreate,
  OrganizationsUsersFetch,
  OrganizationsFetch
}                             from 'data/Organizations/actions';

import {
  OrganizationSendInvite,
  OrganizationSave,
}                             from 'data/Organization/actions';


import {
  Edit as OrganizationEdit
}                             from 'scenes/Organizations/components';

@connect((state) => ({
  manage: state.Organizations.get('manage'),
  products: fromJS(state.Product.products),
  activeTab: state.Organizations.getIn(['manage', 'activeTab']),
  formErrors: fromJS(getFormSyncErrors(Manage.FORM_NAME)(state) || {}),
  formValues: fromJS(getFormValues(Manage.FORM_NAME)(state) || {}),
}), (dispatch) => ({
  setTab: bindActionCreators(OrganizationsManageSetActiveTab, dispatch),
  submitForm: bindActionCreators(submit, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
  updateManage: bindActionCreators(OrganizationsManageUpdate, dispatch),
  registerField: bindActionCreators(registerField, dispatch),
  fetchProducts: bindActionCreators(ProductsFetch, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  OrganizationSave: bindActionCreators(OrganizationSave, dispatch),
  OrganizationsFetch: bindActionCreators(OrganizationsFetch, dispatch),
  OrganizationsCreate: bindActionCreators(OrganizationsCreate, dispatch),
  OrganizationSendInvite: bindActionCreators(OrganizationSendInvite, dispatch),
  OrganizationsUsersFetch: bindActionCreators(OrganizationsUsersFetch, dispatch),
}))
class Edit extends React.Component {

  static contextTypes = {
    router: PropTypes.object
  };

  static propTypes = {
    setTab: PropTypes.func,
    submitForm: PropTypes.func,
    destroyForm: PropTypes.func,
    updateManage: PropTypes.func,
    registerField: PropTypes.func,
    initializeForm: PropTypes.func,
    fetchProducts: PropTypes.func,
    OrganizationSave: PropTypes.func,
    OrganizationsFetch: PropTypes.func,
    OrganizationsCreate: PropTypes.func,
    OrganizationSendInvite: PropTypes.func,
    OrganizationsUsersFetch: PropTypes.func,

    formErrors: PropTypes.instanceOf(Map),
    formValues: PropTypes.instanceOf(Map),

    products: PropTypes.instanceOf(List),
    manage: PropTypes.instanceOf(Map),
    activeTab: PropTypes.string
  };

  constructor(props) {
    super(props);

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleTabChange = this.handleTabChange.bind(this);
    this.handleSubmitFail = this.handleSubmitFail.bind(this);
    this.handleSubmitSuccess = this.handleSubmitSuccess.bind(this);
  }

  componentWillMount() {

    const loadOrganizations = () => {
      return this.props.OrganizationsFetch();
    };

    const loadProducts = () => {
      return this.props.fetchProducts();
    };

    const loadUsers = () => {
      return this.props.OrganizationsUsersFetch({
        id: this.props.params.id
      });
    };

    const initializeForm = (data) => {
      this.props.initializeForm(Manage.FORM_NAME, {
        name: data.organization.get('name'),
        description: data.organization.get('description'),
        logoUrl: data.organization.get('logoUrl'),
        canCreateOrgs: data.organization.get('canCreateOrgs'),
        products: (data.organization.get('products') || []).map(product => product.get('id')),
        admins: {...data.users}
      });

      this.props.registerField(Manage.FORM_NAME, 'admins', 'Field');
    };

    this.props.updateManage(this.props.manage.set('loading', true));
    loadOrganizations().then(({payload: {data}}) => {
      const organizations = data;
      loadProducts().then(({payload: {data}}) => {
        const products = data;
        loadUsers().then(({payload: {data}}) => {
          const users = data;
          initializeForm({
            organization: fromJS(organizations).find(org => org.get('id') === Number(this.props.params.id)),
            products: fromJS(products),
            users: fromJS(users)
          });
          this.props.updateManage(this.props.manage.set('loading', false));
        });
      });
    });

  }

  componentWillUnmount() {
    this.props.updateManage(
      this.props.manage.set('activeTab', Manage.DEFAULT_TAB)
    );

    this.props.destroyForm(Manage.FORM_NAME);
  }

  TABS = {
    INFO: 'Info',
    PRODUCTS: 'Products',
    ADMINS: 'Admins'
  };

  handleCancel() {
    this.context.router.push('/organizations');
  }

  handleTabChange(tab) {
    this.props.setTab(tab);
  }

  handleSubmitSuccess() {
    this.context.router.push('/organizations?success=true');
  }

  handleSubmitFail() {
    //message.error('Cannot create organization');
  }

  handleSubmit() {
    return new Promise((resolve) => {
      this.props.OrganizationSave({
        ...this.props.formValues.toJS(),
        products: [],
        id: this.props.params.id
      }).then(() => {
        this.props.OrganizationsFetch().then(() => {
          resolve();
        });
      });
    });
  }

  render() {

    if (this.props.manage.get('loading'))
      return null;

    return (
      <OrganizationEdit
        formErrors={this.props.formErrors}
        form={Manage.FORM_NAME}
        onSubmit={this.handleSubmit}
        onSubmitSuccess={this.handleSubmitSuccess}
        onSubmitFail={this.handleSubmitFail}
        onCancel={this.handleCancel}
        edit={true} // hardcoded to hide admins tab
        products={this.props.products}
        onTabChange={this.handleTabChange}
        activeTab={this.props.activeTab}
      />
    );
  }

}

export default Edit;
