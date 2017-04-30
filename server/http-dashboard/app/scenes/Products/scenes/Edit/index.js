import React from 'react';
import './styles.less';

import {connect} from 'react-redux';
import {submit} from 'redux-form';
import {message} from 'antd';
import {bindActionCreators} from 'redux';
import {ProductsUpdateMetadataInfoRead} from 'data/Storage/actions';
import {prepareProductForSave} from 'services/Products';
import * as API from 'data/Product/api';
import {
  ProductSetEdit,
  ProductEditMetadataFieldUpdate,
  ProductEditMetadataFieldsUpdate,
  ProductEditInfoValuesUpdate,
  ProductEditClearFields
} from 'data/Product/actions';
import _ from 'lodash';
import ProductEdit from 'scenes/Products/components/ProductEdit';

@connect((state) => ({
  product: state.Product.edit,
  products: state.Product.products,
  isProductInfoInvalid: state.Product.edit.info.invalid,
  isMetadataInfoRead: state.Storage.products.isMetadataInfoRead,
}), (dispatch) => ({
  submitFormById: bindActionCreators(submit, dispatch),
  Fetch: bindActionCreators(API.ProductsFetch, dispatch),
  Update: bindActionCreators(API.ProductUpdate, dispatch),
  ProductSetEdit: bindActionCreators(ProductSetEdit, dispatch),
  ProductEditClearFields: bindActionCreators(ProductEditClearFields, dispatch),
  updateMetadataInfoReadFlag: bindActionCreators(ProductsUpdateMetadataInfoRead, dispatch),
  ProductEditInfoValuesUpdate: bindActionCreators(ProductEditInfoValuesUpdate, dispatch),
  ProductEditMetadataFieldUpdate: bindActionCreators(ProductEditMetadataFieldUpdate, dispatch),
  ProductEditMetadataFieldsUpdate: bindActionCreators(ProductEditMetadataFieldsUpdate, dispatch),
}))
class ProductCreate extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    isMetadataInfoRead: React.PropTypes.bool,
    isProductInfoInvalid: React.PropTypes.bool,

    metadataFields: React.PropTypes.array,

    Fetch: React.PropTypes.func,
    Update: React.PropTypes.func,
    ProductSetEdit: React.PropTypes.func,
    submitFormById: React.PropTypes.func,
    updateInfoInvalidFlag: React.PropTypes.func,
    ProductEditClearFields: React.PropTypes.func,
    updateMetadataInfoReadFlag: React.PropTypes.func,
    ProductEditInfoValuesUpdate: React.PropTypes.func,
    ProductEditMetadataFieldUpdate: React.PropTypes.func,
    ProductEditMetadataFieldsUpdate: React.PropTypes.func,

    params: React.PropTypes.object,
    products: React.PropTypes.array,
    product: React.PropTypes.object,
  };

  constructor(props) {
    super(props);

    this.state = {
      activeTab: props && props.params.tab || this.TABS.INFO,
    };
  }

  componentWillMount() {
    this.props.Fetch().then(() => {
      if (!this.getProduct())
        this.context.router.push('/products?notFound=true');

      this.props.ProductSetEdit(this.getProduct());
    });
  }

  getProduct() {
    return _.find(this.props.products, {id: Number(this.props.params.id)});
  }

  isMetadataFormInvalid() {
    if (Array.isArray(this.props.product.metadata.fields)) {
      return this.props.product.metadata.fields.some((field) => {
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

  componentWillUnmount() {
    this.props.ProductEditClearFields();
  }

  handleSubmit() {

    if (Array.isArray(this.props.product.metadata.fields)) {
      this.props.product.metadata.fields.forEach((field) => {
        this.props.submitFormById(`metadatafield${field.id}`);
      });
    }

    this.props.submitFormById(`product-edit-info`);

    this.setState({
      submited: true
    });

    if (!this.isMetadataFormInvalid() && !this.isInfoFormInvalid()) {

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
  }

  onInfoValuesChange(values) {
    this.props.ProductEditInfoValuesUpdate(values);
  }

  onMetadataFieldChange(field) {
    this.props.ProductEditMetadataFieldUpdate(field);
  }

  onMetadataFieldsChange(values) {
    this.props.ProductEditMetadataFieldsUpdate(values);
  }

  render() {
    if (!this.props.product.info.values.id)
      return null;

    return (
      <ProductEdit product={this.props.product}
                   isInfoFormInvalid={this.props.isProductInfoInvalid}
                   isMetadataFormInvalid={this.isMetadataFormInvalid()}
                   isMetadataInfoRead={this.props.isMetadataInfoRead}
                   updateMetadataInfoReadFlag={this.props.updateMetadataInfoReadFlag}
                   onInfoValuesChange={this.onInfoValuesChange.bind(this)}
                   onMetadataFieldChange={this.onMetadataFieldChange.bind(this)}
                   onMetadataFieldsChange={this.onMetadataFieldsChange.bind(this)}
                   handleSubmit={this.handleSubmit.bind(this)}
                   handleCancel={this.handleCancel.bind(this)}
                   onTabChange={this.onTabChange.bind(this)}
                   params={this.props.params}/>
    );
  }
}

export default ProductCreate;
