import React from 'react';
import './styles.less';
import {HARDWARES, CONNECTIONS_TYPES} from 'services/Devices';
import {connect} from 'react-redux';
import {submit} from 'redux-form';
import {message} from 'antd';
import {bindActionCreators} from 'redux';
import {ProductsUpdateMetadataInfoRead} from 'data/Storage/actions';
import {prepareProductForSave} from 'services/Products';
import * as API from 'data/Product/api';
import {
  ProductSetEdit,
  ProductEditClearFields,
  ProductEditMetadataFieldUpdate,
  ProductEditMetadataFieldsUpdate,
  ProductEditInfoValuesUpdate
} from 'data/Product/actions';
import ProductCreate from 'scenes/Products/components/ProductCreate';

@connect((state) => ({
  product: state.Product.edit,
  products: state.Product.products,
  isProductInfoInvalid: state.Product.edit.info.invalid,
  isMetadataInfoRead: state.Storage.products.isMetadataInfoRead,
}), (dispatch) => ({
  submitFormById: bindActionCreators(submit, dispatch),
  Fetch: bindActionCreators(API.ProductsFetch, dispatch),
  Create: bindActionCreators(API.ProductCreate, dispatch),
  ProductSetEdit: bindActionCreators(ProductSetEdit, dispatch),
  ProductEditClearFields: bindActionCreators(ProductEditClearFields, dispatch),
  updateMetadataInfoReadFlag: bindActionCreators(ProductsUpdateMetadataInfoRead, dispatch),
  ProductEditInfoValuesUpdate: bindActionCreators(ProductEditInfoValuesUpdate, dispatch),
  ProductEditMetadataFieldUpdate: bindActionCreators(ProductEditMetadataFieldUpdate, dispatch),
  ProductEditMetadataFieldsUpdate: bindActionCreators(ProductEditMetadataFieldsUpdate, dispatch),
}))
class Create extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    isMetadataInfoRead: React.PropTypes.bool,
    isProductInfoInvalid: React.PropTypes.bool,

    Fetch: React.PropTypes.func,
    Create: React.PropTypes.func,
    ProductSetEdit: React.PropTypes.func,
    submitFormById: React.PropTypes.func,
    ProductEditClearFields: React.PropTypes.func,
    updateMetadataInfoReadFlag: React.PropTypes.func,
    ProductEditInfoValuesUpdate: React.PropTypes.func,
    ProductEditMetadataFieldUpdate: React.PropTypes.func,
    ProductEditMetadataFieldsUpdate: React.PropTypes.func,

    params: React.PropTypes.object,
    products: React.PropTypes.array,
    product: React.PropTypes.object,
  };

  componentWillMount() {
    this.props.ProductSetEdit({
      boardType: HARDWARES["Particle Electron"].key,
      connectionType: CONNECTIONS_TYPES["GSM"].key
    });
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
    this.context.router.push(`/products`);
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

      this.props.Create(prepareProductForSave(this.props.product)).then(() => {
        this.context.router.push(`/products/?success=true`);
      }).catch((err) => {
        message.error(err.message || 'Cannot create product');
      });

    }

  }

  componentWillUnmount() {
    this.props.ProductEditClearFields();
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

    if (!this.props.product.info.values.boardType)
      return null;

    return (
      <ProductCreate product={this.props.product}
                     isInfoFormInvalid={this.props.isProductInfoInvalid}
                     isMetadataFormInvalid={this.isMetadataFormInvalid()}
                     isMetadataInfoRead={this.props.isMetadataInfoRead}
                     updateMetadataInfoReadFlag={this.props.updateMetadataInfoReadFlag}
                     onInfoValuesChange={this.onInfoValuesChange.bind(this)}
                     onMetadataFieldChange={this.onMetadataFieldChange.bind(this)}
                     onMetadataFieldsChange={this.onMetadataFieldsChange.bind(this)}
                     handleSubmit={this.handleSubmit.bind(this)}
                     handleCancel={this.handleCancel.bind(this)}
                     params={this.props.params}/>
    );
  }

}

export default Create;
