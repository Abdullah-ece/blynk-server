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
  DeviceMetadataUpdate,
  DeviceDetailsUpdate as updateDevice,
} from 'data/Devices/api';
import {DeviceDetailsUpdate} from 'data/Devices/actions';
import {StartLoading, FinishLoading} from 'data/PageLoading/actions';
import {bindActionCreators} from 'redux';
import {Map} from 'immutable';

@connect((state) => ({
  orgId: state.Account.orgId,
  deviceDetails: state.Devices.get('deviceDetails'),
}), (dispatch) => ({
  StartLoading: bindActionCreators(StartLoading, dispatch),
  FinishLoading: bindActionCreators(FinishLoading, dispatch),
  updateDeviceDetails: bindActionCreators(DeviceDetailsUpdate, dispatch),
  fetchDeviceInfo: bindActionCreators(DeviceDetailsFetch, dispatch),
  DeviceMetadataUpdate: bindActionCreators(DeviceMetadataUpdate, dispatch),
  updateDevice: bindActionCreators(updateDevice, dispatch),
}))
class DeviceDetailsScene extends React.Component {

  static propTypes = {
    params: PropTypes.object,
    location: PropTypes.object,

    orgId: PropTypes.number,

    fetchDeviceInfo: PropTypes.func,
    updateDeviceDetails: PropTypes.func,
    DeviceMetadataUpdate: PropTypes.func,
    updateDevice: PropTypes.func,
    StartLoading: PropTypes.func,
    FinishLoading: PropTypes.func,

    deviceDetails: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.onDeviceChange = this.onDeviceChange.bind(this);
    this.onMetadataChange = this.onMetadataChange.bind(this);
  }

  componentWillMount() {
    this.fetchDevice();
  }

  componentWillUpdate(nextProps) {
    if (nextProps.params.id !== this.props.params.id) {
      this.props.StartLoading();
      this.fetchDevice(nextProps.params.id).then(() => {
        this.props.FinishLoading();
      });
    }
  }

  componentWillUnmount() {
    this.props.updateDeviceDetails(
      this.props.deviceDetails
        .setIn(['info', 'data'], null)
        .setIn(['info', 'loading'], false)
    );
  }

  fetchDevice(id) {
    this.toggleDeviceInfoLoading(true);

    return this.props.fetchDeviceInfo({
      orgId: this.props.orgId,
    }, {
      id: id || this.props.params.id
    }).then(() => {
      this.toggleDeviceInfoLoading(false);
    });
  }

  toggleDeviceInfoLoading(state) {
    this.props.updateDeviceDetails(
      this.props.deviceDetails.setIn(['info', 'loading'], state)
    );
  }

  onDeviceChange(device) {
    return this.props.updateDevice({
      orgId: this.props.orgId
    }, device);
  }

  onMetadataChange(metadata) {
    return new Promise((resolve, reject) => {
      this.props.DeviceMetadataUpdate({
        orgId: this.props.orgId,
        deviceId: this.props.params.id
      }, metadata).then(() => {
        this.fetchDevice(this.props.params.id).then(() => {
          resolve();
        }).catch((err) => reject(err));
      }).catch((err) => reject(err));
    }).catch(() => {
      message.error('Error happened during metadata updating');
    });
  }

  render() {
    if (!this.props.deviceDetails.getIn(['info', 'data']))
      return null;

    return (
      <Device onDeviceChange={this.onDeviceChange}
              onMetadataChange={this.onMetadataChange}
              deviceInfoLoading={this.props.deviceDetails.getIn(['info', 'loading'])}
              device={this.props.deviceDetails.getIn(['info', 'data'])}
              params={this.props.params}
              location={this.props.location}
      />
    );
  }

}

export default DeviceDetailsScene;
