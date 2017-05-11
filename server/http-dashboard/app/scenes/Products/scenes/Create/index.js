import React from 'react';
import './styles.less';
import {HARDWARES, CONNECTIONS_TYPES} from 'services/Devices';
import {connect} from 'react-redux';
import {submit} from 'redux-form';
import {message} from 'antd';
import {bindActionCreators} from 'redux';
import {ProductsUpdateMetadataFirstTime} from 'data/Storage/actions';
import {prepareProductForSave, exampleMetadataField} from 'services/Products';
import * as API from 'data/Product/api';
import {
  ProductSetEdit,
  ProductEditClearFields,
  ProductEditMetadataFieldUpdate,
  ProductEditMetadataFieldsUpdate,
  ProductEditDataStreamsFieldUpdate,
  ProductEditDataStreamsFieldsUpdate,
  ProductEditInfoValuesUpdate
} from 'data/Product/actions';
import ProductCreate from 'scenes/Products/components/ProductCreate';

@connect((state) => ({
  product: state.Product.edit,
  products: state.Product.products,
  isProductInfoInvalid: state.Product.edit.info.invalid,
  isMetadataFirstTime: state.Storage.products.metadataFirstTime,
}), (dispatch) => ({
  submitFormById: bindActionCreators(submit, dispatch),
  Fetch: bindActionCreators(API.ProductsFetch, dispatch),
  Create: bindActionCreators(API.ProductCreate, dispatch),
  ProductSetEdit: bindActionCreators(ProductSetEdit, dispatch),
  ProductEditClearFields: bindActionCreators(ProductEditClearFields, dispatch),
  updateMetadataFirstTimeFlag: bindActionCreators(ProductsUpdateMetadataFirstTime, dispatch),
  ProductEditInfoValuesUpdate: bindActionCreators(ProductEditInfoValuesUpdate, dispatch),
  ProductEditMetadataFieldUpdate: bindActionCreators(ProductEditMetadataFieldUpdate, dispatch),
  ProductEditMetadataFieldsUpdate: bindActionCreators(ProductEditMetadataFieldsUpdate, dispatch),
  ProductEditDataStreamsFieldUpdate: bindActionCreators(ProductEditDataStreamsFieldUpdate, dispatch),
  ProductEditDataStreamsFieldsUpdate: bindActionCreators(ProductEditDataStreamsFieldsUpdate, dispatch),
}))
class Create extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    isMetadataFirstTime: React.PropTypes.bool,
    isProductInfoInvalid: React.PropTypes.bool,

    Fetch: React.PropTypes.func,
    Create: React.PropTypes.func,
    ProductSetEdit: React.PropTypes.func,
    submitFormById: React.PropTypes.func,
    ProductEditClearFields: React.PropTypes.func,
    updateMetadataFirstTimeFlag: React.PropTypes.func,
    ProductEditInfoValuesUpdate: React.PropTypes.func,
    ProductEditMetadataFieldUpdate: React.PropTypes.func,
    ProductEditMetadataFieldsUpdate: React.PropTypes.func,
    ProductEditDataStreamsFieldUpdate: React.PropTypes.func,
    ProductEditDataStreamsFieldsUpdate: React.PropTypes.func,

    params: React.PropTypes.object,
    products: React.PropTypes.array,
    product: React.PropTypes.object,
  };

  componentWillMount() {
    this.props.ProductSetEdit({
      boardType: HARDWARES["Particle Electron"].key,
      connectionType: CONNECTIONS_TYPES["GSM"].key,
      metaFields: this.props.isMetadataFirstTime ? [exampleMetadataField] : [],
      events: [
        {
          name: 'Online name',
          type: "ONLINE",
          isNotificationsEnabled: true
        },
        {
          name: 'Offline name',
          type: "OFFLINE",
          isNotificationsEnabled: true,
          ignorePeriod: '12 hrs 5 min'
        },
        {
          name: "Info event",
          type: "INFORMATION",
          isNotificationsEnabled: true
        },
        {
          name: "Warning event",
          type: "WARNING",
          isNotificationsEnabled: true
        },
        {
          name: "Critical event",
          type: "CRITICAL",
          isNotificationsEnabled: true
        }
      ]
    });
  }

  componentWillUnmount() {
    this.props.ProductEditClearFields();
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
    this.context.router.push(`/products`);
  }

  handleSubmit() {

    if (Array.isArray(this.props.product.metadata.fields)) {
      this.props.product.metadata.fields.forEach((field) => {
        this.props.submitFormById(`metadatafield${field.id}`);
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

    if (!this.isDataStreamsFormInvalid() && !this.isMetadataFormInvalid() && !this.isInfoFormInvalid()) {

      this.props.Create(prepareProductForSave(this.props.product)).then(() => {
        this.context.router.push(`/products/?success=true`);
      }).catch((err) => {
        message.error(err.message || 'Cannot create product');
      });

    }

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

  onDataStreamsFieldChange(field) {
    this.props.ProductEditDataStreamsFieldUpdate(field);
  }

  onDataStreamsFieldsChange(values) {
    this.props.ProductEditDataStreamsFieldsUpdate(values);
  }

  render() {

    if (!this.props.product.info.values.boardType)
      return null;

    return (
      <ProductCreate product={this.props.product}
                     isInfoFormInvalid={this.props.isProductInfoInvalid}
                     isMetadataFormInvalid={this.isMetadataFormInvalid()}
                     isDataStreamsFormInvalid={this.isDataStreamsFormInvalid()}
                     isMetadataInfoRead={!this.props.isMetadataFirstTime}
                     updateMetadataFirstTimeFlag={this.props.updateMetadataFirstTimeFlag}
                     onInfoValuesChange={this.onInfoValuesChange.bind(this)}
                     onMetadataFieldChange={this.onMetadataFieldChange.bind(this)}
                     onMetadataFieldsChange={this.onMetadataFieldsChange.bind(this)}
                     onDataStreamsFieldChange={this.onDataStreamsFieldChange.bind(this)}
                     onDataStreamsFieldsChange={this.onDataStreamsFieldsChange.bind(this)}
                     handleSubmit={this.handleSubmit.bind(this)}
                     handleCancel={this.handleCancel.bind(this)}
                     params={this.props.params}/>
    );
  }

}

export default Create;
