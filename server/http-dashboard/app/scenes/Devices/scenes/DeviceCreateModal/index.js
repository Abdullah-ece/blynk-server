/* eslint-disable no-console */
import React from 'react';
import PropTypes from 'prop-types';
import {
  DeviceCreateModal
} from 'scenes/Devices/components';

import {STATUS, SETUP_PRODUCT_KEY} from 'services/Devices';

import {
  DeviceAvailableOrganizationsFetch,
  DeviceCreate,
  DevicesFetch
} from 'data/Devices/api';

import {
  getFormSyncErrors,
  getFormValues,
  reset,
  change
} from 'redux-form';

import {
  ProductCreate
} from 'data/Product/api';

import {
  connect
} from 'react-redux';

import {
  bindActionCreators
} from 'redux';
import _ from 'lodash';

@connect((state) => ({
  organizations: state.Devices.deviceCreationModal.organizations,
  organization : state.Organization,
  account      : state.Account,
  products     : state.Product.products,
  errors       : getFormSyncErrors('DeviceCreate')(state),
  formValues   : getFormValues('DeviceCreate')(state)
}), (dispatch) => ({
  fetchOrganizationsList: bindActionCreators(DeviceAvailableOrganizationsFetch, dispatch),
  change                : bindActionCreators(change, dispatch),
  resetForm             : bindActionCreators(reset, dispatch),
  fetchDevices          : bindActionCreators(DevicesFetch, dispatch),
  createDevice          : bindActionCreators(DeviceCreate, dispatch),
  createProduct         : bindActionCreators(ProductCreate, dispatch),
}))
class DeviceCreateModalScene extends React.Component {

  static contextTypes = {
    router: PropTypes.object,
  };

  static propTypes = {
    visible: PropTypes.bool,

    formValues  : PropTypes.object,
    errors      : PropTypes.object,
    account     : PropTypes.object,
    organization: PropTypes.object,

    products     : PropTypes.array,
    organizations: PropTypes.array,

    onClose               : PropTypes.func,
    fetchOrganizationsList: PropTypes.func,
    change                : PropTypes.func,
    resetForm             : PropTypes.func,
    fetchDevices          : PropTypes.func,
    createDevice          : PropTypes.func,
    createProduct         : PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleClose = this.handleClose.bind(this);
    this.handleProductSelect = this.handleProductSelect.bind(this);
  }

  state = {
    loading               : false,
    productId             : null,
    previousBoardType     : null,
    previousConnectionType: null
  };

  componentWillMount() {
    this.props.fetchOrganizationsList();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps && nextProps.formValues && nextProps.formValues.productId !== this.state.productId) {
      this.setState({
        productId: nextProps.formValues.productId
      });

      const product = _.find(this.props.products, (product) => {
        return Number(product.id) === Number(nextProps.formValues.productId);
      });

      if (nextProps.formValues.productId === this.SETUP_PRODUCT_KEY && this.props.formValues.productId !== this.SETUP_PRODUCT_KEY) {
        this.props.change('DeviceCreate', 'boardType', this.state.previousBoardType || null);
        this.props.change('DeviceCreate', 'connectionType', this.state.previousConnectionType || null);
      }

      if (nextProps.formValues.productId !== this.SETUP_PRODUCT_KEY && this.props.formValues.productId === this.SETUP_PRODUCT_KEY) {
        this.setState({
          previousBoardType     : this.props.formValues && this.props.formValues.boardType || null,
          previousConnectionType: this.props.formValues && this.props.formValues.connectionType || null
        });
      }

      if (product) {
        this.props.change('DeviceCreate', 'boardType', product.boardType);
        this.props.change('DeviceCreate', 'connectionType', product.connectionType);
      }
    }

    if (this.props.formValues && this.props.formValues.orgId && Number(this.props.formValues.orgId) !== Number(nextProps.formValues.orgId)) {
      this.props.change('DeviceCreate', 'productId', '');
    }

    if (this.props.formValues && !nextProps.formValues.orgId) {
      this.props.change('DeviceCreate','orgId', this.props.organization.id);
    }
  }

  // shouldComponentUpdate(nextProps) {
  //   return (
  //     !_.isEqual(this.props.visible, nextProps.visible) ||
  //     !_.isEqual(this.props.organizations, nextProps.organizations) ||
  //     !_.isEqual(this.props.organization, nextProps.organization) ||
  //     !_.isEqual(this.props.account, nextProps.account) ||
  //     !_.isEqual(this.props.products, nextProps.products) ||
  //     !_.isEqual(this.props.errors, nextProps.errors) ||
  //     !_.isEqual(this.props.formValues, nextProps.formValues)
  //   );
  // }

  SETUP_PRODUCT_KEY = 'SETUP_NEW_PRODUCT';

  redirectToNewDevice(id) {
    this.props.resetForm('DeviceCreate');
    this.props.onClose();
    this.context.router.push(`/devices/${id}`);
  }

  handleClose() {
    this.props.resetForm('DeviceCreate');
    if (typeof this.props.onClose === 'function')
      this.props.onClose();
  }

  handleSubmit() {

    const createDevice = (productId) => {
      this.setState({ loading: true });

      return this.props.createDevice({
        orgId: this.props.formValues.orgId
      }, {
        ...this.props.formValues,
        productId: productId || this.props.formValues.productId,
        status: STATUS.OFFLINE
      }).then((response) => {
        this.props.fetchDevices({
          orgId: this.props.account.selectedOrgId
        }).then(() => {
          if (response.payload.data && response.payload.data.id) {
            this.redirectToNewDevice(response.payload.data.id);
          } else {
            this.handleCancelClick();
          }
        }).catch((err) => {
          console.error(err);
          this.setState({ loading: false });
        });
      }).catch((err) => {
        console.error(err);
        this.setState({ loading: false });
      });
    };

    return new Promise((resolve) => {

      if (this.props.formValues.productId === this.SETUP_PRODUCT_KEY) {
        this.props.createProduct({
          orgId: this.props.organization.id,
          product: {
            "name": `New Product ${_.random(1, 999999999)}`,
            "boardType": this.props.formValues.boardType,
            "connectionType": this.props.formValues.connectionType,
          }
        }).then((response) => {
          createDevice(response.payload.data.id).then(() => {
            this.setState({ loading: false });
            resolve();
          });
        });
      } else {
        createDevice().then(() => {
          this.setState({ loading: false });
          resolve();
        });
      }

    });
  }

  handleProductSelect(productId) {

    const getProductDefaultName = (product) => {
      return product.metaFields.filter((field) => {
        return field.name === "Device Name";
      })[0].value;
    };

    const getProductById = (products, productId) => {
      return products.filter((product)=>{
        return Number(product.id) === Number(productId);
      })[0];
    };

    if(productId !== SETUP_PRODUCT_KEY) {

      const currentProduct = getProductById(this.props.products, this.props.formValues.productId);

      const nextProduct = getProductById(this.props.products, productId);

      if(this.props.formValues.name && this.props.formValues.productId) {
        if(currentProduct && getProductDefaultName(currentProduct) === this.props.formValues.name) {
          this.props.change("DeviceCreate", 'name', getProductDefaultName(nextProduct));
        }
      } else {
        this.props.change("DeviceCreate", 'name', getProductDefaultName(nextProduct));
      }
    }
  }
  render() {

    const {
      visible,
      organizations,
      organization,
      formValues,
      products,
      errors
    } = this.props;

    const initialValues = {
      productId: null,
      orgId    : organization.id || null
    };

    return (
      <DeviceCreateModal
        errors={errors}
        visible={visible}
        formValues={formValues}
        products={products}
        organizations={organizations}
        organization={organization}
        onClose={this.handleClose}
        handleSubmit={this.handleSubmit}
        initialValues={initialValues}
        onProductSelect={this.handleProductSelect}
        loading={this.state.loading}
      />
    );
  }

}

export default DeviceCreateModalScene;
