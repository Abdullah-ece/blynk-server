import React from 'react';
import PropTypes from 'prop-types';
// import {
//   message,
// } from 'antd';
import {
  Device
} from 'scenes/Devices/components';
import {connect} from 'react-redux';
import {
  DeviceDetailsFetch,
  DeviceProductsFetch
//   DeviceMetadataUpdate,
//   DeviceDetailsUpdate as updateDevice,
} from 'data/Devices/api';
// import {DeviceDetailsUpdate} from 'data/Devices/actions';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';
import {bindActionCreators} from 'redux';
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
  fetchDevice: bindActionCreators(DeviceDetailsFetch, dispatch),
  fetchProducts: bindActionCreators(DeviceProductsFetch, dispatch)
  // DeviceMetadataUpdate: bindActionCreators(DeviceMetadataUpdate, dispatch),
  // updateDevice: bindActionCreators(updateDevice, dispatch),
}))
class DeviceDetailsScene extends React.Component {

  static propTypes = {
    device: PropTypes.object,
    params: PropTypes.object,
    location: PropTypes.object,
    //
    orgId: PropTypes.number,
    productId: PropTypes.number,
    //
    fetchDevice: PropTypes.func,
    fetchProducts: PropTypes.func,
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
    return this.props.fetchDevice({
      orgId: this.props.orgId,
    }, {
      id: id || this.props.params.id
    }).then(() => {
      this.props.finishLoading();
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

  render() {

    if(!this.props.device)
      return null;

    return (
      <Device params={this.props.params}
              location={this.props.location}
              productId={this.props.productId}
      />
    );
  }

}

export default DeviceDetailsScene;
