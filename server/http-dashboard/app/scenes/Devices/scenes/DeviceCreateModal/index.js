/* eslint-disable no-console */
import React from 'react';
import PropTypes from 'prop-types';
import {
  DeviceCreateModal
} from 'scenes/Devices/components';
import {displayError} from "services/ErrorHandling";
import {message} from "antd";

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

      const product = _.find(this.props.organization.products, (product) => {
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

    if (this.props.formValues && this.props.account.selectedOrgId && Number(this.props.account.selectedOrgId) !== Number(nextProps.account.selectedOrgId)) {
      this.props.change('DeviceCreate', 'productId', '');
    }

    if (this.props.organization.id != nextProps.organization.id) {
      this.props.change('DeviceCreate','orgId', nextProps.organization.id);
    }
  }

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
        orgId: this.props.account.selectedOrgId
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
          this.setState({ loading: false });
          displayError(err, message.error);
        });
      }).catch((err) => {
        this.setState({ loading: false });
        displayError(err, message.error);
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

      const currentProduct = getProductById(this.props.organization.products, this.props.formValues.productId);

      const nextProduct = getProductById(this.props.organization.products, productId);

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
      errors,
      account
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
        products={organization.products}
        account={account}
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
