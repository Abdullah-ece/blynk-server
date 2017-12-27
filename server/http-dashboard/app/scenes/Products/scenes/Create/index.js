import React from 'react';
import {
  fromJS,
  // Map
} from 'immutable';
import './styles.less';

import {
  // HARDWARES,
  // CONNECTIONS_TYPES,
  AVAILABLE_HARDWARE_TYPES_LIST,
  AVAILABLE_CONNECTION_TYPES_LIST,
} from 'services/Devices';

import {connect} from 'react-redux';
// import _ from 'lodash';
import {
  // submit,
  getFormSyncErrors,
  initialize,
  // destroy,
  getFormValues,
  // isDirty,
  // reduxForm
} from 'redux-form';
import {message} from 'antd';
import {bindActionCreators} from 'redux';
// import {ProductsUpdateMetadataFirstTime} from 'data/Storage/actions';
import {
  FORMS,
  // prepareProductForSave,
  // exampleMetadataField,
  // getHardcodedRequiredMetadataFields,
  // TABS,
  PRODUCT_CREATE_INITIAL_VALUES,
} from 'services/Products';
// import {OrganizationFetch} from 'data/Organization/actions';
import * as API from 'data/Product/api';
// import {
//   ProductSetEdit,
//   ProductEditClearFields,
//   ProductEditEventsFieldsUpdate,
//   ProductEditMetadataFieldUpdate,
//   ProductEditMetadataFieldsUpdate,
//   ProductEditDataStreamsFieldUpdate,
//   ProductEditDataStreamsFieldsUpdate,
//   ProductEditInfoValuesUpdate,
// } from 'data/Product/actions';

import ProductCreate from 'scenes/Products/components/ProductCreate';
import PropTypes from 'prop-types';
import ImmutablePropTypes from 'react-immutable-proptypes';


@connect((state) => {
  return {
    formValues: fromJS(getFormValues(FORMS.PRODUCTS_PRODUCT_CREATE)(state) || {}),
    formSyncErrors: fromJS(getFormSyncErrors(FORMS.PRODUCTS_PRODUCT_CREATE)(state) || {}),
    // organization: fromJS(state.Organization),
    orgId: state.Account.orgId,
    // product: state.Product.edit,
    // products: state.Product.products,
    // Organization: state.Organization,
    // isProductInfoInvalid: state.Product.edit.info.invalid,
    // isMetadataFirstTime: state.Storage.products.metadataFirstTime,
    // dashboard: fromJS(getFormValues(FORMS.DASHBOARD)(state) || {}),
  };
}, (dispatch) => ({
  // submitFormById: bindActionCreators(submit, dispatch),
  // Fetch: bindActionCreators(API.ProductsFetch, dispatch),
  Create: bindActionCreators(API.ProductCreate, dispatch),
  // destroyForm: bindActionCreators(destroy, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  // ProductSetEdit: bindActionCreators(ProductSetEdit, dispatch),
  // OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch),
  // ProductEditClearFields: bindActionCreators(ProductEditClearFields, dispatch),
  // updateMetadataFirstTimeFlag: bindActionCreators(ProductsUpdateMetadataFirstTime, dispatch),
  // ProductEditInfoValuesUpdate: bindActionCreators(ProductEditInfoValuesUpdate, dispatch),
  // ProductEditEventsFieldsUpdate: bindActionCreators(ProductEditEventsFieldsUpdate, dispatch),
  // ProductEditMetadataFieldUpdate: bindActionCreators(ProductEditMetadataFieldUpdate, dispatch),
  // ProductEditMetadataFieldsUpdate: bindActionCreators(ProductEditMetadataFieldsUpdate, dispatch),
  // ProductEditDataStreamsFieldUpdate: bindActionCreators(ProductEditDataStreamsFieldUpdate, dispatch),
  // ProductEditDataStreamsFieldsUpdate: bindActionCreators(ProductEditDataStreamsFieldsUpdate, dispatch),
}))
class Create extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {

    formValues: ImmutablePropTypes.contains({
      name: PropTypes.string,
      boardType: PropTypes.oneOf(AVAILABLE_HARDWARE_TYPES_LIST),
      connectionType: PropTypes.oneOf(AVAILABLE_CONNECTION_TYPES_LIST),
      description: PropTypes.string,
      logoUrl: PropTypes.string,
    }),

    formSyncErrors: PropTypes.object,

    loading: PropTypes.bool,
    invalid: PropTypes.bool,
    submitting: PropTypes.bool,

    Create: PropTypes.func,
    initializeForm: PropTypes.func,

    orgId: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number,
    ])

    // isFormDirty: React.PropTypes.bool,
    // isMetadataFirstTime: React.PropTypes.bool,
    // isProductInfoInvalid: React.PropTypes.bool,
    //
    // values: React.PropTypes.object,
    // Fetch: React.PropTypes.func,
    // destroyForm: React.PropTypes.func,
    // ProductSetEdit: React.PropTypes.func,
    // submitFormById: React.PropTypes.func,
    // OrganizationFetch: React.PropTypes.func,
    // ProductEditClearFields: React.PropTypes.func,
    // updateMetadataFirstTimeFlag: React.PropTypes.func,
    // ProductEditInfoValuesUpdate: React.PropTypes.func,
    // ProductEditEventsFieldsUpdate: React.PropTypes.func,
    // ProductEditMetadataFieldUpdate: React.PropTypes.func,
    // ProductEditMetadataFieldsUpdate: React.PropTypes.func,
    // ProductEditDataStreamsFieldUpdate: React.PropTypes.func,
    // ProductEditDataStreamsFieldsUpdate: React.PropTypes.func,
    //
    // dashboard: React.PropTypes.instanceOf(Map),
    // organization: React.PropTypes.instanceOf(Map),
    // params: React.PropTypes.object,
    // products: React.PropTypes.array,
    // eventsForms: React.PropTypes.array,
    // product: React.PropTypes.object,
    // Organization: React.PropTypes.object,
    // router: React.PropTypes.object,
    // route: React.PropTypes.object,
    //
    // orgId: React.PropTypes.any
  };

  constructor(props) {
    super(props);

    // this.routerWillLeave = this.routerWillLeave.bind(this);

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    // this.onInfoValuesChange = this.onInfoValuesChange.bind(this);
    // this.onMetadataFieldChange = this.onMetadataFieldChange.bind(this);
    // this.onMetadataFieldsChange = this.onMetadataFieldsChange.bind(this);
    // this.onEventsFieldsChange = this.onEventsFieldsChange.bind(this);
    // this.onDataStreamsFieldChange = this.onDataStreamsFieldChange.bind(this);
    // this.onDataStreamsFieldsChange = this.onDataStreamsFieldsChange.bind(this);
  }

  componentWillMount() {

    this.props.initializeForm(FORMS.PRODUCTS_PRODUCT_CREATE, {
      ...PRODUCT_CREATE_INITIAL_VALUES,
    });

    //
    // const checkForPermission = (org) => {
    //   if (org && org.parentId > 0) {
    //     this.context.router.push('/products');
    //   }
    // };
    //
    // if (this.props.Organization.parentId === null) {
    //   this.props.OrganizationFetch({id: this.props.orgId}).then(() => {
    //     checkForPermission(this.props.Organization);
    //   });
    // } else {
    //   checkForPermission(this.props.Organization);
    // }
    //
    // const hardcodedMetadataPredefinedValues = {
    //   timezoneDefaultValue: this.props.organization.get('tzName'),
    //   manufacturerDefaultValue: this.props.organization.get('name')
    // };
    //
    // this.props.ProductSetEdit({
    //   boardType: HARDWARES["Particle Electron"].key,
    //   connectionType: CONNECTIONS_TYPES["GSM"].key,
    //   metaFields: this.props.isMetadataFirstTime ? getHardcodedRequiredMetadataFields(hardcodedMetadataPredefinedValues).concat(exampleMetadataField) : getHardcodedRequiredMetadataFields(hardcodedMetadataPredefinedValues),
    //   events: [
    //     {
    //       id: 1,
    //       name: '',
    //       type: "ONLINE"
    //     },
    //     {
    //       id: 2,
    //       name: '',
    //       type: "OFFLINE",
    //       ignorePeriod: '0 hrs 0 min'
    //     }
    //   ]
    // });
    //
    // this.props.initializeForm(FORMS.DASHBOARD, {
    //   widgets: []
    // });
    //
    // this.props.router.setRouteLeaveHook(
    //   this.props.route,
    //   this.routerWillLeave
    // );
  }

  // componentWillUnmount() {
    // this.props.destroyForm(FORMS.DASHBOARD);
    // this.props.ProductEditClearFields();
  // }

  // routerWillLeave(route) {
    // const regexp = /products\/edit\/[0-9]+\/(info|metadata|datastreams|events|dashboard)/g;
    //
    // if(!this.isProductCreated && this.props.isFormDirty && !regexp.test(route.pathname))
    //   return 'Leave this page without saving your changes?';
  // }

  // isMetadataFormInvalid() {
    // if (Array.isArray(this.props.product.metadata.fields)) {
    //   return this.props.product.metadata.fields.some((field) => {
    //     return field.invalid;
    //   });
    // }
    // return false;
  // }

  // isEventsFormInvalid() {
    // if (Array.isArray(this.props.eventsForms)) {
    //   return this.props.eventsForms.some((form) => !!form.syncErrors);
    // }
    // return false;
  // }

  // isDataStreamsFormInvalid() {
  //   if (Array.isArray(this.props.product.dataStreams.fields)) {
  //     return this.props.product.dataStreams.fields.some((field) => {
  //       return field.invalid;
  //     });
  //   }
  //   return false;
  // }

  // isInfoFormInvalid() {
  //   return this.props.isProductInfoInvalid;
  // }
  //
  // handleCancel() {
  //   this.context.router.push(`/products`);
  // }

  // loading = false;

  // handleSubmit() {
  //   this.loading = true;
  //   if (Array.isArray(this.props.product.metadata.fields)) {
  //     this.props.product.metadata.fields.forEach((field) => {
  //       this.props.submitFormById(`metadatafield${field.id}`);
  //     });
  //   }
  //
  //   if (Array.isArray(this.props.product.events.fields)) {
  //     this.props.product.events.fields.forEach((field) => {
  //       this.props.submitFormById(`event${field.id}`);
  //     });
  //   }
  //
  //   if (Array.isArray(this.props.product.dataStreams.fields)) {
  //     this.props.product.dataStreams.fields.forEach((field) => {
  //       this.props.submitFormById(`datastreamfield${field.id}`);
  //     });
  //   }
  //
  //   this.props.submitFormById(`product-edit-info`);
  //
  //   this.setState({
  //     submited: true
  //   });
  //
  //   if (!this.isDataStreamsFormInvalid() && !this.isMetadataFormInvalid() && !this.isInfoFormInvalid() && !this.isEventsFormInvalid()) {
  //
  //     this.props.Create({
  //       product: prepareProductForSave({
  //         ...this.props.product,
  //         webDashboard: {
  //           ...this.props.dashboard.updateIn(['widgets'], (widgets) => (
  //             widgets.map((widget) => ({
  //               ...widget.toJS(),
  //               width: widget.get('w'),
  //               height: widget.get('h'),
  //             }))
  //           )).toJS()
  //         }
  //       }),
  //       orgId: this.props.orgId
  //     }).then(() => {
  //       this.isProductCreated = true;
  //       this.loading = false;
  //       this.context.router.push(`/products/?success=true`);
  //     }).catch((response) => {
  //       message.error(response && response.error && response.error.response.message || 'Cannot create product');
  //     });
  //
  //   }
  //
  // }
  //
  // onInfoValuesChange(values) {
  //   this.props.ProductEditInfoValuesUpdate(values);
  // }
  //
  // onMetadataFieldChange(field) {
  //   this.props.ProductEditMetadataFieldUpdate(field);
  // }
  //
  // onMetadataFieldsChange(values) {
  //   this.props.ProductEditMetadataFieldsUpdate(values);
  // }
  //
  // onEventsFieldsChange(field) {
  //   this.props.ProductEditEventsFieldsUpdate(field);
  // }
  //
  // onDataStreamsFieldChange(field) {
  //   this.props.ProductEditDataStreamsFieldUpdate(field);
  // }
  //
  // onDataStreamsFieldsChange(values) {
  //   this.props.ProductEditDataStreamsFieldsUpdate(values);
  // }

  handleCancel() {
    this.context.router.push(`/products`);
  }

  handleSubmit() {

    return this.props.Create({
      product: this.props.formValues.toJS(),
      orgId: this.props.orgId,
    }).then(() => {
      this.context.router.push(`/products/?success=true`);
    }).catch((response) => {
      message.error(response && response.error && response.error.response.message || 'Cannot create product');
    });
  }

  render() {
    //
    // const params = {
    //   id: Number(this.props.params.id) || 0,
    //   tab: String(this.props.params.tab || TABS.INFO),
    // };
    //
    // if (!this.props.product.info.values.boardType)
    //   return null;

    // if(this.props.values){
    //   params.title = this.props.values.name && this.props.values.name !== "" ? this.props.values.name : "New Product";
    // } else {
    //   params.title = "New Product";
    // }

    return (
      <ProductCreate
        formValues={this.props.formValues}
        invalid={this.props.invalid}
        loading={this.props.loading}
        submitting={this.props.submitting}
        onSubmit={this.handleSubmit}
        onCancel={this.handleCancel}
        form={FORMS.PRODUCTS_PRODUCT_CREATE}
        formSyncErrors={this.props.formSyncErrors}
        // product={this.props.product}
        // isInfoFormInvalid={this.props.isProductInfoInvalid}
        // isEventsFormInvalid={this.isEventsFormInvalid()}
        // isMetadataFormInvalid={this.isMetadataFormInvalid()}
        // isDataStreamsFormInvalid={this.isDataStreamsFormInvalid()}
        // isMetadataInfoRead={!this.props.isMetadataFirstTime}
        // updateMetadataFirstTimeFlag={this.props.updateMetadataFirstTimeFlag}
        // onInfoValuesChange={this.onInfoValuesChange}
        // onEventsFieldsChange={this.onEventsFieldsChange}
        // onMetadataFieldChange={this.onMetadataFieldChange}
        // onMetadataFieldsChange={this.onMetadataFieldsChange}
        // onDataStreamsFieldChange={this.onDataStreamsFieldChange}
        // onDataStreamsFieldsChange={this.onDataStreamsFieldsChange}
        // handleSubmit={this.handleSubmit}
        // handleCancel={this.handleCancel}
        // params={params}
        // loading={this.loading}
      />
    );
  }

}

export default Create;
