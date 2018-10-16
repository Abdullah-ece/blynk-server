import React from 'react';
import PropTypes from 'prop-types';
import {
  message,
} from 'antd';
import {
  Device
} from 'scenes/Devices/components';
import {connect} from 'react-redux';
import {
  DeviceDetailsFetch,
  DeviceProductsFetch,
  DeviceFetch,
//   DeviceMetadataUpdate,
//   DeviceDetailsUpdate as updateDevice,
} from 'data/Devices/api';
import {TABS, TAB_URLS} from 'services/Devices';
// import {DeviceDetailsUpdate} from 'data/Devices/actions';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';
import {bindActionCreators} from 'redux';
import {displayError} from "services/ErrorHandling";
// import {Map} from 'immutable';

@connect((state) => ({
  device: state.Devices.deviceDetails,
  orgId: state.Account.orgId,
  productId: state.Devices.deviceDetails.productId,
  // deviceDetails: state.Devices.get('deviceDetails'),
}), (dispatch) => ({
  startLoading: bindActionCreators(StartLoading, dispatch),
  finishLoading: bindActionCreators(FinishLoading, dispatch),
  // updateDeviceDetails: bindActionCreators(DeviceDetailsUpdate, dispatch),
  fetchDeviceDetails: bindActionCreators(DeviceDetailsFetch, dispatch),
  updateDevice:bindActionCreators(DeviceFetch,dispatch),
  fetchProducts: bindActionCreators(DeviceProductsFetch, dispatch)
  // DeviceMetadataUpdate: bindActionCreators(DeviceMetadataUpdate, dispatch),
  // updateDevice: bindActionCreators(updateDevice, dispatch),
}))
class DeviceDetailsScene extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    device: PropTypes.object,
    params: PropTypes.object,
    location: PropTypes.object,
    //
    orgId: PropTypes.number,
    productId: PropTypes.number,
    //
    fetchDeviceDetails: PropTypes.func,
    onDeviceDelete: PropTypes.func,
    fetchProducts: PropTypes.func,
    updateDevice: PropTypes.func,
    // updateDeviceDetails: PropTypes.func,
    // DeviceMetadataUpdate: PropTypes.func,
    // updateDevice: PropTypes.func,
    startLoading: PropTypes.func,
    finishLoading: PropTypes.func,
    //
    // deviceDetails: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);
    this.handleTabChange = this.handleTabChange.bind(this);
    // this.onDeviceChange = this.onDeviceChange.bind(this);
    // this.onMetadataChange = this.onMetadataChange.bind(this);
  }

  componentWillMount() {
    this.props.fetchProducts();
    this.fetchDevice();
  }

  componentWillUpdate(nextProps) {
    if (nextProps.params.id !== this.props.params.id) {
      this.fetchDevice(nextProps.params.id);
    }
    if (this.context.router.params.tab === TABS.TIMELINE) {
      this.props.updateDevice({
        orgId: this.props.orgId,
      }, {
        id: this.props.params.id
      });
    }
  }

  // componentWillUnmount() {
  //   this.props.updateDeviceDetails(
  //     this.props.deviceDetails
  //       .setIn(['info', 'data'], null)
  //       .setIn(['info', 'loading'], false)
  //   );
  // }

  fetchDevice(id) {
    this.props.startLoading();
    return this.props.fetchDeviceDetails({
      orgId: this.props.orgId,
    }, {
      id: id || this.props.params.id
    }).then(() => {
      this.props.finishLoading();
    }).catch((err) => {
      displayError(err, message.error);
    });
  }

  // toggleDeviceInfoLoading(state) {
  //   this.props.updateDeviceDetails(
  //     this.props.deviceDetails.setIn(['info', 'loading'], state)
  //   );
  // }

  // onDeviceChange(device) {
  //   return this.props.updateDevice({
  //     orgId: this.props.orgId
  //   }, device);
  // }

  // onMetadataChange(metadata) {
  //   return new Promise((resolve, reject) => {
  //     this.props.DeviceMetadataUpdate({
  //       orgId: this.props.orgId,
  //       deviceId: this.props.params.id
  //     }, metadata).then(() => {
  //       this.fetchDevice(this.props.params.id).then(() => {
  //         resolve();
  //       }).catch((err) => reject(err));
  //     }).catch((err) => reject(err));
  //   }).catch(() => {
  //     message.error('Can\'t update Metadata due to an error');
  //   });
  // }
  handleTabChange(tab) {
    if (tab === TABS.TIMELINE) {
      this.props.updateDevice({
        orgId: this.props.orgId,
      }, {
         id: this.props.params.id
      });
    }
    this.context.router.push(`/devices/${this.props.params.id}/${TAB_URLS[tab]}`);
  }
  render() {

    if(!this.props.device)
      return null;

    return (
      <Device params={this.props.params}
              onDeviceDelete={this.props.onDeviceDelete}
              location={this.props.location}
              productId={this.props.productId}
              onTabChange={this.handleTabChange}
      />
    );
  }

}

export default DeviceDetailsScene;
