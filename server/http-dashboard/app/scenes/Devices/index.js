import React from 'react';
import {
  Index
} from './scenes';
import _ from 'lodash';

import PropTypes from 'prop-types';

import FullSizeLoading from 'scenes/FullSizeLoading';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {PreloadDevicesFetch} from "data/Devices/api";
import {ProductsPreloadFetch} from "data/Product/api";

@connect((state) => ({
  productsLoading: state.Product.productsPreloadLoading,
  devicesLoading: state.Devices.devicesPreloadLoading,
  orgId: state.Account.orgId,
}), (dispatch) => ({
  fetchDevices: bindActionCreators(PreloadDevicesFetch, dispatch),
  fetchProducts: bindActionCreators(ProductsPreloadFetch, dispatch),
}))
class Devices extends React.Component {

  static propTypes = {
    params       : PropTypes.object,
    location     : PropTypes.object,
    fetchDevices : PropTypes.func,
    fetchProducts: PropTypes.func,
    orgId        : PropTypes.number,
    devicesLoading      : PropTypes.bool,
    productsLoading      : PropTypes.bool,
  };

  componentWillMount() {
    this.props.fetchDevices({
      orgId: this.props.orgId
    });
    this.props.fetchProducts();
  }

  shouldComponentUpdate(nextProps) {
    return (
      !_.isEqual(nextProps.params, this.props.params) ||
      !_.isEqual(nextProps.location, this.props.location) ||
      !_.isEqual(nextProps.loading, this.props.devicesLoading) ||
      !_.isEqual(nextProps.loading, this.props.productsLoading) ||
      !_.isEqual(nextProps.orgId, this.props.orgId)
    );
  }

  render() {

    const { params, location } = this.props;

    if(this.props.devicesLoading || this.props.productsLoading) {
      return (
        <FullSizeLoading placeholder={'Loading Devices'}/>
      );
    }

    return (
      <Index params={params} location={location}/>
    );
  }

}

export default Devices;
