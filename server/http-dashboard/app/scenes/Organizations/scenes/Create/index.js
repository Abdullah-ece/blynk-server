import React                  from 'react';
import {connect}              from 'react-redux';
import {bindActionCreators}   from 'redux';
import {
  initialize,
  destroy,
  getFormSyncErrors,
  getFormAsyncErrors,
  getFormSubmitErrors,
  submit,
  getFormValues,
  registerField,
  SubmissionError
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
  OrganizationSendInvite
}                             from 'data/Organization/actions';

import {message} from 'antd';

import {
  Create as OrganizationCreate
}                             from 'scenes/Organizations/components';

import './styles.less';

@connect((state) => ({
  account: state.Account,
  manage: state.Organizations.get('manage'),
  products: fromJS(state.Product.products),
  activeTab: state.Organizations.getIn(['manage', 'activeTab']),
  formErrors: fromJS(getFormSyncErrors(Manage.FORM_NAME)(state) || {}),
  formAsyncErrors: fromJS(getFormAsyncErrors(Manage.FORM_NAME)(state) || {}),
  formSubmitErrors: fromJS(getFormSubmitErrors(Manage.FORM_NAME)(state) || {}),
  formValues: fromJS(getFormValues(Manage.FORM_NAME)(state) || {}),
}), (dispatch) => ({
  setTab: bindActionCreators(OrganizationsManageSetActiveTab, dispatch),
  submitForm: bindActionCreators(submit, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
  updateManage: bindActionCreators(OrganizationsManageUpdate, dispatch),
  registerField: bindActionCreators(registerField, dispatch),
  fetchProducts: bindActionCreators(ProductsFetch, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  OrganizationsFetch: bindActionCreators(OrganizationsFetch, dispatch),
  OrganizationsCreate: bindActionCreators(OrganizationsCreate, dispatch),
  OrganizationSendInvite: bindActionCreators(OrganizationSendInvite, dispatch),
}))
class Create extends React.Component {

  static contextTypes = {
    router: PropTypes.object
  };

  static propTypes = {
    account: PropTypes.object,
    setTab: PropTypes.func,
    submitForm: PropTypes.func,
    destroyForm: PropTypes.func,
    updateManage: PropTypes.func,
    registerField: PropTypes.func,
    initializeForm: PropTypes.func,
    fetchProducts: PropTypes.func,
    OrganizationsFetch: PropTypes.func,
    OrganizationsCreate: PropTypes.func,
    OrganizationSendInvite: PropTypes.func,

    formErrors: PropTypes.instanceOf(Map),
    formAsyncErrors: PropTypes.instanceOf(Map),
    formSubmitErrors: PropTypes.instanceOf(Map),
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
    this.props.fetchProducts({
      orgId: this.props.account.selectedOrgId
    });

    this.props.initializeForm(Manage.FORM_NAME, {
      name: 'New Organization',
      selectedProducts: [],
      admins: []
    });

    this.props.registerField(Manage.FORM_NAME, 'admins', 'Field');
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
    return (new Promise((resolve, reject) => {
      this.props.OrganizationsCreate({
        ...this.props.formValues.toJS()
      }).then((organization) => {
        this.props.OrganizationsFetch().then(() => {
          const promises = [];
          this.props.formValues.get('admins').forEach((admin) => {
            promises.push(this.props.OrganizationSendInvite({...admin.toJS(), id: organization.payload.data.id}));
          });
          Promise.all(promises).then(() => {
            resolve();
          }).catch((response) => {
            message.error((<span>An Error occurred while sending the invites for admins. <br/>You can resend invites on Organization Users page</span>), 5);
            reject({invites: response});
          });
        }).catch((response) => {
          reject({orgFetch: response});
        });
      }).catch((response) => {
        reject({orgCreate: response});
      });
    })).catch((err) => {

      if (err && err.orgCreate) {
        throw new SubmissionError({
          name: err.orgCreate.error.response.data.error.message
        });
      }

    });
  }

  render() {
    return (
      <OrganizationCreate
        formErrors={this.props.formErrors}
        formAsyncErrors={this.props.formAsyncErrors}
        formSubmitErrors={this.props.formSubmitErrors}
        formValues={this.props.formValues}
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
