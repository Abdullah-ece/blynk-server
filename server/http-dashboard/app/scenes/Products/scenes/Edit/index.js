import React from 'react';
import './styles.less';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import {
  // submit,
  getFormSyncErrors,
  // initialize,
  // destroy,
  // getFormValues,
  // isDirty,
} from 'redux-form';

import {fromJS, Map} from 'immutable';
import {message} from 'antd';
import {MainLayout} from 'components';
// import {ProductsUpdateMetadataFirstTime} from 'data/Storage/actions';
// import {OrganizationFetch} from 'data/Organization/actions';
import {
  // repareProductForSave,
  // TABS,
  DEVICE_FORCE_UPDATE,
  FORMS
} from 'services/Products';
// import {prepareWidgetForProductEdit} from 'services/Widgets';
import * as API from 'data/Product/api';
// import {
//   ProductSetEdit,
//   ProductEditEventsFieldsUpdate,
//   ProductEditMetadataFieldUpdate,
//   ProductEditMetadataFieldsUpdate,
//   ProductEditDataStreamsFieldUpdate,
//   ProductEditDataStreamsFieldsUpdate,
//   ProductEditInfoValuesUpdate,
//   ProductEditClearFields,
// } from 'data/Product/actions';
// import _ from 'lodash';
import ProductEdit from 'scenes/Products/components/ProductEdit';
import PropTypes from 'prop-types';

@connect((state, ownProps) => {
  return {

    formSyncErrors:  fromJS(getFormSyncErrors(FORMS.PRODUCTS_PRODUCT_MANAGE)(state) || {}),

    product: (() => {
      const products = fromJS(state.Product.products);

      let product = products.find((product) => Number(product.get('id')) === Number(ownProps.params.id));

      if(product) {

        if(!product.has('metaFields'))
          product = product.set('metaFields', []);

        if(!product.has('dataStreams'))
          product = product.set('dataStreams', []);

        if(!product.has('events'))
          product = product.set('events', []);

        if(!product.has('webDashboard'))
          product = product.set('webDashboard', {});

        if(!product.hasIn(['webDashboard', 'widgets']))
          product = product.setIn(['webDashboard', 'widgets'], []);

        return product;

      } else {
        throw new Error('Cannot find product for Edit');
      }
    })(),

    orgId: state.Account.orgId,

//     formValues: fromJS(getFormValues(FORMS.PRODUCTS_PRODUCT_MANAGE)(state) || {}),
//     formSyncErrors: fromJS(getFormSyncErrors(FORMS.PRODUCTS_PRODUCT_MANAGE)(state) || {}),
//
//     organization: fromJS(state.Organization),
//
//     // organization: fromJS(state.Organization),
//
//     // product: state.Product.edit,
//     // products: state.Product.products,
//     // Organization: state.Organization,
//     // isProductInfoInvalid: state.Product.edit.info.invalid,
//     // isMetadataFirstTime: state.Storage.products.metadataFirstTime,
//     // dashboard: fromJS(getFormValues(FORMS.DASHBOARD)(state) || {}),
  };
}, (dispatch) => ({
//   // submitFormById: bindActionCreators(submit, dispatch),
//   // Fetch: bindActionCreators(API.ProductsFetch, dispatch),
  Create: bindActionCreators(API.ProductCreate, dispatch),
  Update: bindActionCreators(API.ProductUpdate, dispatch),
  Delete: bindActionCreators(API.ProductDelete, dispatch),
  updateDevicesByProduct: bindActionCreators(API.ProductUpdateDevices, dispatch),
//   // destroyForm: bindActionCreators(destroy, dispatch),
//   initializeForm: bindActionCreators(initialize, dispatch),
//   // ProductSetEdit: bindActionCreators(ProductSetEdit, dispatch),
//   // OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch),
//   // ProductEditClearFields: bindActionCreators(ProductEditClearFields, dispatch),
//   // updateMetadataFirstTimeFlag: bindActionCreators(ProductsUpdateMetadataFirstTime, dispatch),
//   // ProductEditInfoValuesUpdate: bindActionCreators(ProductEditInfoValuesUpdate, dispatch),
//   // ProductEditEventsFieldsUpdate: bindActionCreators(ProductEditEventsFieldsUpdate, dispatch),
//   // ProductEditMetadataFieldUpdate: bindActionCreators(ProductEditMetadataFieldUpdate, dispatch),
//   // ProductEditMetadataFieldsUpdate: bindActionCreators(ProductEditMetadataFieldsUpdate, dispatch),
//   // ProductEditDataStreamsFieldUpdate: bindActionCreators(ProductEditDataStreamsFieldUpdate, dispatch),
//   // ProductEditDataStreamsFieldsUpdate: bindActionCreators(ProductEditDataStreamsFieldsUpdate, dispatch),
}))
class Edit extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    product: PropTypes.instanceOf(Map),

    params: PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      tab: PropTypes.string,
    }),

    orgId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

    formSyncErrors: PropTypes.instanceOf(Map),

    Create: PropTypes.func,
    Update: PropTypes.func,
    Delete: PropTypes.func,
    updateDevicesByProduct: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      isDeleteDialogVisible: false,
      isDevicesForceUpdateVisible: false,
      deviceForceUpdateLoading: false
    };

    // this.routerWillLeave = this.routerWillLeave.bind(this);
    //
    // this.handleProductDeviceForceUpdateCancel = this.handleProductDeviceForceUpdateCancel.bind(this);
    // this.handleProductDeviceForceUpdateSubmit = this.handleProductDeviceForceUpdateSubmit.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleDelete = this.handleDelete.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleHideDeleteDialog = this.handleHideDeleteDialog.bind(this);
    this.handleShowDeleteDialog = this.handleShowDeleteDialog.bind(this);
    // this.handleDeleteSubmit = this.handleDeleteSubmit.bind(this);
    this.handleTabChange = this.handleTabChange.bind(this);
    // this.onInfoValuesChange = this.onInfoValuesChange.bind(this);
    // this.onMetadataFieldChange = this.onMetadataFieldChange.bind(this);
    // this.onMetadataFieldsChange = this.onMetadataFieldsChange.bind(this);
    // this.onEventsFieldsChange = this.onEventsFieldsChange.bind(this);
    // this.onDataStreamsFieldChange = this.onDataStreamsFieldChange.bind(this);
    // this.onDataStreamsFieldsChange = this.onDataStreamsFieldsChange.bind(this);
    this.handleProductSaveSuccess = this.handleProductSaveSuccess.bind(this);
    this.handleDevicesForceUpdateSubmit = this.handleDevicesForceUpdateSubmit.bind(this);
    this.handleDevicesForceUpdateCancel = this.handleDevicesForceUpdateCancel.bind(this);
  }

  componentWillMount() {

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
    // this.props.Fetch().then(() => {
    //
    //   const product = this.getProduct();
    //
    //   if (!product)
    //     this.context.router.push('/products?notFound=true');
    //
    //   this.props.ProductSetEdit(product);
    //
    //   let widgets = product.webDashboard && product.webDashboard.widgets && product.webDashboard.widgets.map(prepareWidgetForProductEdit) || [];
    //
    //   this.props.initializeForm(FORMS.DASHBOARD, {
    //     widgets: widgets
    //   });
    //
    // });
    //
    // this.props.router.setRouteLeaveHook(
    //   this.props.route,
    //   this.routerWillLeave
    // );
  }

  componentWillUnmount() {
    // this.props.ProductEditClearFields();
  }

  // routerWillLeave(route) {
  //   const regexp = /products\/edit\/[0-9]+\/(info|metadata|datastreams|events|dashboard)/g;
  //   if(!this.isProductSaved && this.props.isFormDirty && !regexp.test(route.pathname))
  //     return 'Leave this page without saving your changes?';
  // }

  // getProduct() {
    // return _.find(this.props.products, {id: Number(this.props.params.id)});
  // }

  // isEventsFormInvalid() {
    // if (Array.isArray(this.props.eventsForms)) {
    //   return this.props.eventsForms.some((form) => !!form.syncErrors);
    // }
    // return false;
  // }

  // isMetadataFormInvalid() {
  //   if (Array.isArray(this.props.product.metadata.fields)) {
  //     return this.props.product.metadata.fields.some((field) => {
  //       return field.invalid;
  //     });
  //   }
  //   return false;
  // }
  //
  // isDataStreamsFormInvalid() {
  //   if (Array.isArray(this.props.product.dataStreams.fields)) {
  //     return this.props.product.dataStreams.fields.some((field) => {
  //       return field.invalid;
  //     });
  //   }
  //   return false;
  // }
  //
  // isInfoFormInvalid() {
  //   return this.props.isProductInfoInvalid;
  // }
  //
  handleCancel() {
    if (this.state.activeTab) {
      this.context.router.push(`/product/${this.props.params.id}/${this.state.activeTab}`);
    } else {
      this.context.router.push(`/product/${this.props.params.id}`);
    }
  }
  //
  handleSubmit(product) {
    if (this.doesProductHaveDevices()) {
      this.setState({
        isDevicesForceUpdateVisible: true
      });

      this.handleDevicesForceUpdateSubmit = this.handleDevicesForceUpdateSubmit.bind(this, product);
    } else {
      this.saveProductWithoutDevicesUpdate(product);
    }

  }
  //
  handleTabChange(key) {
    this.context.router.push(`/products/edit/${this.props.params.id}/${key}`);
  }
  //
  // onInfoValuesChange(values) {
  //   this.props.ProductEditInfoValuesUpdate(values);
  // }
  //
  // onDataStreamsFieldChange(field) {
  //   this.props.ProductEditDataStreamsFieldUpdate(field);
  // }
  //
  // onDataStreamsFieldsChange(values) {
  //   this.props.ProductEditDataStreamsFieldsUpdate(values);
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

  handleDevicesForceUpdateSubmit(product, value) {
    if (value === DEVICE_FORCE_UPDATE.UPDATE_DEVICES) {
      this.saveProductAndUpdateDevices(product);
    } else if (value === DEVICE_FORCE_UPDATE.SAVE_WITHOUT_UPDATE) {
      this.saveProductWithoutDevicesUpdate(product);
    } else if (value === DEVICE_FORCE_UPDATE.CLONE_PRODUCT) {
      this.cloneProductWithoutSaving(product);
    }
  }

  doesProductHaveDevices() {
    return this.props.product.get('deviceCount') || 0;
  }

  saveProduct(product) {
    this.setState({
      deviceForceUpdateLoading: true
    });
    return this.props.Update(product);
  }

  updateDevicesByProduct(product) {
    return this.props.updateDevicesByProduct(product);
  }

  handleProductSaveSuccess(response) {
    const { tab } = this.props.params;

    this.isProductSaved = true;

    if (tab) {
      this.context.router.push(`/product/${response.payload.data.id}/${tab}?save=true`);
    } else {
      this.context.router.push(`/product/${response.payload.data.id}?save=true`);
    }
  }

  // getDashboardValues() {
  //   return {
  //     webDashboard: {
  //       ...this.props.dashboard.updateIn(['widgets'], (widgets) => (
  //         widgets.map((widget) => ({
  //           ...widget.toJS(),
  //           width: widget.get('w'),
  //           height: widget.get('h'),
  //         }))
  //       )).toJS()
  //     }
  //   };
  // }
  //
  saveProductAndUpdateDevices(product) {

    this.saveProduct({
      product: product,
      orgId: this.props.orgId
    }).then(() => {
      this.updateDevicesByProduct({
        product: product,
        orgId: this.props.orgId
      })
        .then(this.handleProductSaveSuccess)
        .catch((response) => {
          this.setState({
            deviceForceUpdateLoading: false
          });
          message.error(response && response.error && response.error.response.message || 'Cannot save product');
        });
    }).catch((response) => {
      this.setState({
        deviceForceUpdateLoading: false
      });
      message.error(response && response.error && response.error.response.message || 'Cannot save product');
    });
  }

  saveProductWithoutDevicesUpdate(product) {
    this.saveProduct({
      product: product,
      orgId: this.props.orgId
    })
      .then(this.handleProductSaveSuccess)
      .catch((response) => {
        message.error(response && response.error && response.error.response.message || 'Cannot save product');
      });
  }

  cloneProductWithoutSaving(product) {
    product.name = `${product.name} Copy`;

    this.props.Create({
      product: product,
      orgId: this.props.orgId
    }).then(this.handleProductSaveSuccess)
      .catch((response) => {
        this.setState({
          deviceForceUpdateLoading: false
        });
        message.error(response && response.error && response.error.response.message || 'Cannot clone product');
      });

  }
  //
  handleDevicesForceUpdateCancel() {
    this.setState({
      isDevicesForceUpdateVisible: false
    });
  }
  //
  handleDelete() {
    return this.props.Delete(this.props.params.id).then(() => {
      this.context.router.push('/products?deleted=true');
    }).catch((err) => {
      message.error(err.message || 'Cannot delete product');
    });
  }

  handleHideDeleteDialog() {
    this.setState({
      isDeleteDialogVisible: false
    });
  }

  handleShowDeleteDialog() {
    this.setState({
      isDeleteDialogVisible: true
    });
  }



  render() {
    // if (!this.props.product.info.values.id)
    //   return null;
    //
    // const params = {
    //   id: Number(this.props.params.id),
    //   tab: String(this.props.params.tab)
    // };
    //

    return (
      <MainLayout>
        <ProductEdit form={FORMS.PRODUCTS_PRODUCT_MANAGE}
                     initialValues={this.props.product.toJS()}
                     activeTab={this.props.params.tab}
                     formSyncErrors={this.props.formSyncErrors}
                     onTabChange={this.handleTabChange}
                     onCancel={this.handleCancel}
                     onSubmit={this.handleSubmit}
                     onDelete={this.handleDelete}
                     isDeleteDialogVisible={this.state.isDeleteDialogVisible}
                     onHideDeleteDialog={this.handleHideDeleteDialog}
                     onShowDeleteDialog={this.handleShowDeleteDialog}
                     isDevicesForceUpdateVisible={this.state.isDevicesForceUpdateVisible}
                     deviceForceUpdateLoading={this.state.deviceForceUpdateLoading}
                     onDevicesForceUpdateSubmit={this.handleDevicesForceUpdateSubmit}
                     onDevicesForceUpdateCancel={this.handleDevicesForceUpdateCancel}
    //                  invalid={this.props.invalid}
    //                  loading={this.props.loading}
    //                  submitting={this.props.submitting}
    //                  onCancel={this.handleCancel}
    //                  form={FORMS.PRODUCTS_PRODUCT_MANAGE}
    //                  formSyncErrors={this.props.formSyncErrors}
    //                  params={params}
        />

      </MainLayout>
    );

  }
}

export default Edit;
