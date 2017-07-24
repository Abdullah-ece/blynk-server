import React                  from 'react';
import {connect}              from 'react-redux';
import {bindActionCreators}   from 'redux';
import {message}              from 'antd';
import {
  initialize,
  destroy,
  getFormSyncErrors,
  submit,
  getFormValues
}              from 'redux-form';
import {Map, List, fromJS}    from 'immutable';
import PropTypes              from 'prop-types';
import {ProductsFetch}        from 'data/Product/api';
import {Manage}               from 'services/Organizations';

import {
  OrganizationsManageSetActiveTab,
  OrganizationsManageUpdate,
  OrganizationsCreate,
  OrganizationsFetch
}                             from 'data/Organizations/actions';

import {
  Create as OrganizationCreate
}                             from 'scenes/Organizations/components';

import './styles.less';

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
  fetchProducts: bindActionCreators(ProductsFetch, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  OrganizationsFetch: bindActionCreators(OrganizationsFetch, dispatch),
  OrganizationsCreate: bindActionCreators(OrganizationsCreate, dispatch),
}))
class Create extends React.Component {

  static contextTypes = {
    router: PropTypes.object
  };

  static propTypes = {
    setTab: PropTypes.func,
    submitForm: PropTypes.func,
    destroyForm: PropTypes.func,
    updateManage: PropTypes.func,
    initializeForm: PropTypes.func,
    fetchProducts: PropTypes.func,
    OrganizationsFetch: PropTypes.func,
    OrganizationsCreate: PropTypes.func,

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
    this.props.fetchProducts();

    this.props.initializeForm(Manage.FORM_NAME, {
      name: 'New Organization',
      products: []
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
    message.error('Cannot create organization');
  }

  handleSubmit() {
    return new Promise((resolve) => {
      this.props.OrganizationsCreate({
        ...this.props.formValues.toJS(),
        products: []
      }).then(() => {
        this.props.OrganizationsFetch().then(() => {
          resolve();
        });
      });
    });
  }

  render() {
    return (
      <OrganizationCreate
        formErrors={this.props.formErrors}
        form={Manage.FORM_NAME}
        onSubmit={this.handleSubmit}
        onSubmitSuccess={this.handleSubmitSuccess}
        onSubmitFail={this.handleSubmitFail}
        onCancel={this.handleCancel}
        products={this.props.products}
        onTabChange={this.handleTabChange}
        activeTab={this.props.activeTab}
      />
    );
  }

}

export default Create;
