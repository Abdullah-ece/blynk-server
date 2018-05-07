import React from 'react';
import OTA from './components';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {ProductInfoDevicesOTAFetch} from 'data/Product/api';

@connect((state) => ({
  orgId: state.Account.orgId,
  devices: state.Product.OTADevices.data,
  devicesLoading: state.Product.OTADevices.loading,
  devicesFetchStatus: state.Product.OTADevices.status,
}), (dispatch) => ({
  fetchDevices: bindActionCreators(ProductInfoDevicesOTAFetch, dispatch)
}))
class OTAScene extends React.Component {

  static propTypes = {
    devices: PropTypes.arrayOf(PropTypes.shape({
      id            : PropTypes.number,
      name          : PropTypes.string,
      status        : PropTypes.oneOf(['ONLINE', 'OFFLINE']), // use this for column "status" and display like a green / gray dot
      disconnectTime: PropTypes.number, // display "Was online N days ago" when user do mouseover the gray dot (idea is to display last time when device was online if it's offline right now)
      hardwareInfo  : PropTypes.shape({
        version: PropTypes.string
      })
    })),

    orgId: PropTypes.number,

    devicesLoading: PropTypes.bool,

    devicesFetchStatus: PropTypes.bool,

    fetchDevices: PropTypes.func,
  };

  componentWillMount() {
    if(!isNaN(Number(this.props.orgId))) {
      this.fetchDevices();
    }
  }

  componentWillReceiveProps(nextProps) {
    if(isNaN(Number(this.props.orgId)) && !isNaN(Number(nextProps.orgId))) {
      this.props.fetchDevices();
    }
  }

  fetchDevices() {
    this.props.fetchDevices({
      orgId: this.props.orgId
    }).catch(() => {
      console.log('Cannot fetch Devices for OTA update');
    });
  }

  render() {

    const {devices, devicesLoading, devicesFetchStatus} = this.props;

    return (
      <OTA devices={devices} devicesLoading={devicesLoading} devicesFetchStatus={devicesFetchStatus}/>
    );
  }
}

export default OTAScene;
