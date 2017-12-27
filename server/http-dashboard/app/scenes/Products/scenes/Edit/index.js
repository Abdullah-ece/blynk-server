import React from 'react';
import './styles.less';

import {connect} from 'react-redux';
import {
  submit,
  getFormSyncErrors,
  initialize,
  destroy,
  getFormValues,
  isDirty,
} from 'redux-form';
import {fromJS} from 'immutable';
import {message} from 'antd';
import {bindActionCreators} from 'redux';
import {MainLayout} from 'components';
import {ProductsUpdateMetadataFirstTime} from 'data/Storage/actions';
import {OrganizationFetch} from 'data/Organization/actions';
import {prepareProductForSave, TABS, DEVICE_FORCE_UPDATE, FORMS} from 'services/Products';
import {prepareWidgetForProductEdit} from 'services/Widgets';
import * as API from 'data/Product/api';
import {
  ProductSetEdit,
  ProductEditEventsFieldsUpdate,
  ProductEditMetadataFieldUpdate,
  ProductEditMetadataFieldsUpdate,
  ProductEditDataStreamsFieldUpdate,
  ProductEditDataStreamsFieldsUpdate,
  ProductEditInfoValuesUpdate,
  ProductEditClearFields,
} from 'data/Product/actions';
import _ from 'lodash';
import ProductEdit from 'scenes/Products/components/ProductEdit';
import ProductDevicesForceUpdate from 'scenes/Products/components/ProductDevicesForceUpdate';

@connect((state) => {

  let eventsForms = [];

  if (state.Product.edit.events && Array.isArray(state.Product.edit.events.fields)) {
    eventsForms = state.Product.edit.events.fields.map((field) => {
      return {
        syncErrors: getFormSyncErrors(`event${field.id}`)(state)
      };
    });
  }

  return {
    Organization: state.Organization,
    orgId: state.Account.orgId,
    product: state.Product.edit,
    products: state.Product.products,
    isProductInfoInvalid: state.Product.edit.info.invalid,
    eventsForms: eventsForms,
    isMetadataFirstTime: state.Storage.products.metadataFirstTime,
    dashboard: fromJS(getFormValues(FORMS.DASHBOARD)(state) || {widgets: []}),
    isFormDirty: (() => {

      const getIds = (entity) => {
        return entity.map((entity) => entity.id);
      };

      const prefixes = {
        'metadata': 'metadatafield',
        'events': 'event',
        'dataStreams': 'datastreamfield'
      };

      const ids = {
        metadata: [],
        dataStreams: [],
        events: []
      };

      const forms = [
        'product-edit-info',
        FORMS.DASHBOARD,
      ];

      const entity = state.Product.edit.entity || {};

      _.forEach(prefixes, (value, prefix) => {
        if (state.Product.edit[prefix] && Array.isArray(state.Product.edit[prefix].fields)) {
          state.Product.edit[prefix].fields.forEach((field) => {
            ids[prefix].push(field.id);
            forms.push(`${value}${field.id}`);
          });
        }
      });

      if (entity.metaFields && !_.isEqual(getIds(entity.metaFields).sort(), ids.metadata.sort())) {
        return true;
      }

      if (entity.dataStreams && !_.isEqual(getIds(entity.dataStreams).sort(), ids.dataStreams.sort())) {
        return true;
      }

      if (entity.events && !_.isEqual(getIds(entity.events).sort(), ids.events.sort())) {
        return true;
      }

      return forms.some((formName) => {
        return isDirty(formName)(state);
      });

    })()
  };
}, (dispatch) => ({
  submitFormById: bindActionCreators(submit, dispatch),
  Fetch: bindActionCreators(API.ProductsFetch, dispatch),
  Update: bindActionCreators(API.ProductUpdate, dispatch),
  Delete: bindActionCreators(API.ProductDelete, dispatch),
  destroyForm: bindActionCreators(destroy, dispatch),
  initializeForm: bindActionCreators(initialize, dispatch),
  Create: bindActionCreators(API.ProductCreate, dispatch),
  updateDevicesByProduct: bindActionCreators(API.ProductUpdateDevices, dispatch),
  ProductSetEdit: bindActionCreators(ProductSetEdit, dispatch),
  OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch),
  ProductEditClearFields: bindActionCreators(ProductEditClearFields, dispatch),
  updateMetadataFirstTimeFlag: bindActionCreators(ProductsUpdateMetadataFirstTime, dispatch),
  ProductEditInfoValuesUpdate: bindActionCreators(ProductEditInfoValuesUpdate, dispatch),
  ProductEditMetadataFieldUpdate: bindActionCreators(ProductEditMetadataFieldUpdate, dispatch),
  ProductEditEventsFieldsUpdate: bindActionCreators(ProductEditEventsFieldsUpdate, dispatch),
  ProductEditMetadataFieldsUpdate: bindActionCreators(ProductEditMetadataFieldsUpdate, dispatch),
  ProductEditDataStreamsFieldUpdate: bindActionCreators(ProductEditDataStreamsFieldUpdate, dispatch),
  ProductEditDataStreamsFieldsUpdate: bindActionCreators(ProductEditDataStreamsFieldsUpdate, dispatch),
}))
class Edit extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    isMetadataFirstTime: React.PropTypes.bool,
    isProductInfoInvalid: React.PropTypes.bool,
    isFormDirty: React.PropTypes.bool,

    metadataFields: React.PropTypes.array,

    dashboard: React.PropTypes.any,
    Fetch: React.PropTypes.func,
    Update: React.PropTypes.func,
    Delete: React.PropTypes.func,
    Create: React.PropTypes.func,
    destroyForm: React.PropTypes.func,
    initializeForm: React.PropTypes.func,
    ProductSetEdit: React.PropTypes.func,
    submitFormById: React.PropTypes.func,
    OrganizationFetch: React.PropTypes.func,
    updateInfoInvalidFlag: React.PropTypes.func,
    ProductEditClearFields: React.PropTypes.func,
    updateDevicesByProduct: React.PropTypes.func,
    updateMetadataFirstTimeFlag: React.PropTypes.func,
    ProductEditInfoValuesUpdate: React.PropTypes.func,
    ProductEditEventsFieldsUpdate: React.PropTypes.func,
    ProductEditMetadataFieldUpdate: React.PropTypes.func,
    ProductEditMetadataFieldsUpdate: React.PropTypes.func,
    ProductEditDataStreamsFieldUpdate: React.PropTypes.func,
    ProductEditDataStreamsFieldsUpdate: React.PropTypes.func,

    params: React.PropTypes.object,
    products: React.PropTypes.array,
    product: React.PropTypes.object,
    eventsForms: React.PropTypes.array,
    Organization: React.PropTypes.object,
    router: React.PropTypes.object,
    route: React.PropTypes.object,

    orgId: React.PropTypes.any
  };

  constructor(props) {
    super(props);

    this.state = {
      activeTab: props && props.params.tab || TABS.INFO,
      isDevicesForceUpdateVisible: false,
      deviceForceUpdateLoading: false
    };

    this.routerWillLeave = this.routerWillLeave.bind(this);

    this.handleProductDeviceForceUpdateCancel = this.handleProductDeviceForceUpdateCancel.bind(this);
    this.handleProductDeviceForceUpdateSubmit = this.handleProductDeviceForceUpdateSubmit.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleDeleteSubmit = this.handleDeleteSubmit.bind(this);
    this.onTabChange = this.onTabChange.bind(this);
    this.onInfoValuesChange = this.onInfoValuesChange.bind(this);
    this.onMetadataFieldChange = this.onMetadataFieldChange.bind(this);
    this.onMetadataFieldsChange = this.onMetadataFieldsChange.bind(this);
    this.onEventsFieldsChange = this.onEventsFieldsChange.bind(this);
    this.onDataStreamsFieldChange = this.onDataStreamsFieldChange.bind(this);
    this.onDataStreamsFieldsChange = this.onDataStreamsFieldsChange.bind(this);
    this.handleProductSaveSuccess = this.handleProductSaveSuccess.bind(this);
  }

  componentWillMount() {

    const checkForPermission = (org) => {
      if (org && org.parentId > 0) {
        this.context.router.push('/products');
      }
    };

    if (this.props.Organization.parentId === null) {
      this.props.OrganizationFetch({id: this.props.orgId}).then(() => {
        checkForPermission(this.props.Organization);
      });
    } else {
      checkForPermission(this.props.Organization);
    }

    this.props.Fetch().then(() => {

      const product = this.getProduct();

      if (!product)
        this.context.router.push('/products?notFound=true');

      this.props.ProductSetEdit(product);

      let widgets = product.webDashboard && product.webDashboard.widgets && product.webDashboard.widgets.map(prepareWidgetForProductEdit) || [];

      this.props.initializeForm(FORMS.DASHBOARD, {
        widgets: widgets
      });

    });

    this.props.router.setRouteLeaveHook(
      this.props.route,
      this.routerWillLeave
    );
  }

  componentWillUnmount() {
    this.props.ProductEditClearFields();
  }

  routerWillLeave(route) {
    const regexp = /products\/edit\/[0-9]+\/(info|metadata|datastreams|events|dashboard)/g;
    if(!this.isProductSaved && this.props.isFormDirty && !regexp.test(route.pathname))
      return 'Leave this page without saving your changes?';
  }

  getProduct() {
    return _.find(this.props.products, {id: Number(this.props.params.id)});
  }

  isEventsFormInvalid() {
    if (Array.isArray(this.props.eventsForms)) {
      return this.props.eventsForms.some((form) => !!form.syncErrors);
    }
    return false;
  }

  isMetadataFormInvalid() {
    if (Array.isArray(this.props.product.metadata.fields)) {
      return this.props.product.metadata.fields.some((field) => {
        return field.invalid;
      });
    }
    return false;
  }

  isDataStreamsFormInvalid() {
    if (Array.isArray(this.props.product.dataStreams.fields)) {
      return this.props.product.dataStreams.fields.some((field) => {
        return field.invalid;
      });
    }
    return false;
  }

  isInfoFormInvalid() {
    return this.props.isProductInfoInvalid;
  }

  handleCancel() {
    if (this.state.activeTab) {
      this.context.router.push(`/product/${this.props.params.id}/${this.state.activeTab}`);
    } else {
      this.context.router.push(`/product/${this.props.params.id}`);
    }
  }

  handleSubmit() {

    if (Array.isArray(this.props.product.metadata.fields)) {
      this.props.product.metadata.fields.forEach((field) => {
        this.props.submitFormById(`metadatafield${field.id}`);
      });
    }

    if (Array.isArray(this.props.product.events.fields)) {
      this.props.product.events.fields.forEach((field) => {
        this.props.submitFormById(`event${field.id}`);
      });
    }

    if (Array.isArray(this.props.product.dataStreams.fields)) {
      this.props.product.dataStreams.fields.forEach((field) => {
        this.props.submitFormById(`datastreamfield${field.id}`);
      });
    }

    this.props.submitFormById(`product-edit-info`);

    this.setState({
      submited: true
    });

    if (!this.isDataStreamsFormInvalid() && !this.isMetadataFormInvalid() && !this.isInfoFormInvalid() && !this.isEventsFormInvalid()) {

      if (this.doesProductHaveDevices()) {
        this.setState({
          isDevicesForceUpdateVisible: true
        });
      } else {
        this.saveProductWithoutDevicesUpdate();
      }


    }

  }

  onTabChange(key) {
    this.setState({
      activeTab: key
    });

    this.context.router.push(`/products/edit/${this.props.params.id}/${key}`);
  }

  onInfoValuesChange(values) {
    this.props.ProductEditInfoValuesUpdate(values);
  }

  onDataStreamsFieldChange(field) {
    this.props.ProductEditDataStreamsFieldUpdate(field);
  }

  onDataStreamsFieldsChange(values) {
    this.props.ProductEditDataStreamsFieldsUpdate(values);
  }

  onMetadataFieldChange(field) {
    this.props.ProductEditMetadataFieldUpdate(field);
  }

  onMetadataFieldsChange(values) {
    this.props.ProductEditMetadataFieldsUpdate(values);
  }

  onEventsFieldsChange(field) {
    this.props.ProductEditEventsFieldsUpdate(field);
  }

  handleProductDeviceForceUpdateSubmit(value) {
    if (value === DEVICE_FORCE_UPDATE.UPDATE_DEVICES) {
      this.saveProductAndUpdateDevices();
    } else if (value === DEVICE_FORCE_UPDATE.SAVE_WITHOUT_UPDATE) {
      this.saveProductWithoutDevicesUpdate();
    } else if (value === DEVICE_FORCE_UPDATE.CLONE_PRODUCT) {
      this.cloneProductWithoutSaving();
    }
  }

  doesProductHaveDevices() {
    let product = _.find(this.props.products, product => Number(product.id) === Number(this.props.params.id));

    return product.deviceCount;
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
    this.isProductSaved = true;
    if (this.state.activeTab) {
      this.context.router.push(`/product/${response.payload.data.id}/${this.state.activeTab}?save=true`);
    } else {
      this.context.router.push(`/product/${response.payload.data.id}?save=true`);
    }
  }

  getDashboardValues() {
    return {
      webDashboard: {
        ...this.props.dashboard.updateIn(['widgets'], (widgets) => (
          widgets.map((widget) => ({
            ...widget.toJS(),
            width: widget.get('w'),
            height: widget.get('h'),
          }))
        )).toJS()
      }
    };
  }

  saveProductAndUpdateDevices() {
    let product = prepareProductForSave({...this.props.product, ...this.getDashboardValues()});

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

  saveProductWithoutDevicesUpdate() {
    let product = prepareProductForSave({...this.props.product, ...this.getDashboardValues()});

    this.saveProduct({
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
  }

  cloneProductWithoutSaving() {
    let product = prepareProductForSave({...this.props.product, ...this.getDashboardValues()});

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

  handleProductDeviceForceUpdateCancel() {
    this.setState({
      isDevicesForceUpdateVisible: false
    });
  }

  handleDeleteSubmit() {
    return this.props.Delete(this.props.params.id).then(() => {
      this.context.router.push('/products?deleted=true');
    }).catch((err) => {
      message.error(err.message || 'Cannot delete product');
    });
  }

  render() {
    if (!this.props.product.info.values.id)
      return null;

    const params = {
      id: Number(this.props.params.id),
      tab: String(this.props.params.tab)
    };

    return (
      <MainLayout>
        <ProductEdit product={this.props.product}
                     isInfoFormInvalid={this.props.isProductInfoInvalid}
                     isEventsFormInvalid={this.isEventsFormInvalid()}
                     isMetadataFormInvalid={this.isMetadataFormInvalid()}
                     isDataStreamsFormInvalid={this.isDataStreamsFormInvalid()}
                     isMetadataInfoRead={!this.props.isMetadataFirstTime}
                     updateMetadataFirstTimeFlag={this.props.updateMetadataFirstTimeFlag}
                     isFormDirty={this.props.isFormDirty}
                     onInfoValuesChange={this.onInfoValuesChange}
                     onMetadataFieldChange={this.onMetadataFieldChange}
                     onMetadataFieldsChange={this.onMetadataFieldsChange}
                     onEventsFieldsChange={this.onEventsFieldsChange}
                     onDataStreamsFieldChange={this.onDataStreamsFieldChange}
                     onDataStreamsFieldsChange={this.onDataStreamsFieldsChange}
                     handleSubmit={this.handleSubmit}
                     handleCancel={this.handleCancel}
                     onTabChange={this.onTabChange}
                     params={params}
                     loading={this.state.deviceForceUpdateLoading}
                     onDelete = {this.handleDeleteSubmit}/>
        <ProductDevicesForceUpdate
          isModalVisible={this.state.isDevicesForceUpdateVisible}
          loading={this.state.deviceForceUpdateLoading}
          product={_.find(this.props.products, product => Number(product.id) === Number(this.props.params.id))}
          onSave={this.handleProductDeviceForceUpdateSubmit}
          onCancel={this.handleProductDeviceForceUpdateCancel}/>
      </MainLayout>
    );
  }
}

export default Edit;
