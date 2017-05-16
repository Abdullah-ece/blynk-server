import React from 'react';
import './styles.less';

import {connect} from 'react-redux';
import {submit, getFormSyncErrors} from 'redux-form';
import {message} from 'antd';
import {bindActionCreators} from 'redux';
import {ProductsUpdateMetadataFirstTime} from 'data/Storage/actions';
import {prepareProductForSave, TABS} from 'services/Products';
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
    product: state.Product.edit,
    products: state.Product.products,
    isProductInfoInvalid: state.Product.edit.info.invalid,
    eventsForms: eventsForms,
    isMetadataFirstTime: state.Storage.products.metadataFirstTime,
  }
}, (dispatch) => ({
  submitFormById: bindActionCreators(submit, dispatch),
  Fetch: bindActionCreators(API.ProductsFetch, dispatch),
  Update: bindActionCreators(API.ProductUpdate, dispatch),
  ProductSetEdit: bindActionCreators(ProductSetEdit, dispatch),
  ProductEditClearFields: bindActionCreators(ProductEditClearFields, dispatch),
  updateMetadataFirstTimeFlag: bindActionCreators(ProductsUpdateMetadataFirstTime, dispatch),
  ProductEditInfoValuesUpdate: bindActionCreators(ProductEditInfoValuesUpdate, dispatch),
  ProductEditMetadataFieldUpdate: bindActionCreators(ProductEditMetadataFieldUpdate, dispatch),
  ProductEditEventsFieldsUpdate: bindActionCreators(ProductEditEventsFieldsUpdate, dispatch),
  ProductEditMetadataFieldsUpdate: bindActionCreators(ProductEditMetadataFieldsUpdate, dispatch),
  ProductEditDataStreamsFieldUpdate: bindActionCreators(ProductEditDataStreamsFieldUpdate, dispatch),
  ProductEditDataStreamsFieldsUpdate: bindActionCreators(ProductEditDataStreamsFieldsUpdate, dispatch),
}))
class ProductCreate extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    isMetadataFirstTime: React.PropTypes.bool,
    isProductInfoInvalid: React.PropTypes.bool,

    metadataFields: React.PropTypes.array,

    Fetch: React.PropTypes.func,
    Update: React.PropTypes.func,
    ProductSetEdit: React.PropTypes.func,
    submitFormById: React.PropTypes.func,
    updateInfoInvalidFlag: React.PropTypes.func,
    ProductEditClearFields: React.PropTypes.func,
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
  };

  constructor(props) {
    super(props);

    this.state = {
      activeTab: props && props.params.tab || TABS.INFO,
    };
  }

  componentWillMount() {
    this.props.Fetch().then(() => {
      if (!this.getProduct())
        this.context.router.push('/products?notFound=true');

      this.props.ProductSetEdit(this.getProduct());
    });
  }

  componentWillUnmount() {
    this.props.ProductEditClearFields();
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
        console.log('submit event', field.id)
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

      this.props.Update(prepareProductForSave(this.props.product)).then(() => {
        if (this.state.activeTab) {
          this.context.router.push(`/product/${this.props.params.id}/${this.state.activeTab}?save=true`);
        } else {
          this.context.router.push(`/product/${this.props.params.id}?save=true`);
        }
      }).catch((err) => {
        message.error(err.message || 'Cannot save product');
      });

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

  render() {
    if (!this.props.product.info.values.id)
      return null;

    return (
      <ProductEdit product={this.props.product}
                   isInfoFormInvalid={this.props.isProductInfoInvalid}
                   isEventsFormInvalid={this.isEventsFormInvalid()}
                   isMetadataFormInvalid={this.isMetadataFormInvalid()}
                   isDataStreamsFormInvalid={this.isDataStreamsFormInvalid()}
                   isMetadataInfoRead={!this.props.isMetadataFirstTime}
                   updateMetadataFirstTimeFlag={this.props.updateMetadataFirstTimeFlag}
                   onInfoValuesChange={this.onInfoValuesChange.bind(this)}
                   onMetadataFieldChange={this.onMetadataFieldChange.bind(this)}
                   onMetadataFieldsChange={this.onMetadataFieldsChange.bind(this)}
                   onEventsFieldsChange={this.onEventsFieldsChange.bind(this)}
                   onDataStreamsFieldChange={this.onDataStreamsFieldChange.bind(this)}
                   onDataStreamsFieldsChange={this.onDataStreamsFieldsChange.bind(this)}
                   handleSubmit={this.handleSubmit.bind(this)}
                   handleCancel={this.handleCancel.bind(this)}
                   onTabChange={this.onTabChange.bind(this)}
                   params={this.props.params}/>
    );
  }
}

export default ProductCreate;
