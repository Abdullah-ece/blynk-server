import React from 'react';
import {DeviceName} from './components';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {DeviceDetailsUpdate} from 'data/Devices/api';
import PropTypes from 'prop-types';

@connect((state) => ({
  orgId: state.Organization.id,
  device: state.Devices.deviceDetails
}), (dispatch) => ({
  updateDevice: bindActionCreators(DeviceDetailsUpdate, dispatch)
}))
class DeviceNameScene extends React.Component {

  static propTypes = {
    orgId: PropTypes.number,

    device: PropTypes.object,

    updateDevice: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleDeviceNameChange = this.handleDeviceNameChange.bind(this);
  }

  handleDeviceNameChange(name) {
    this.props.updateDevice({
      orgId: this.props.orgId
    }, {
      ...this.props.device,
      name
    });
  }

  render() {

    if(!this.props.device || !this.props.device.name)
      return null;

    return (
      <DeviceName value={this.props.device.name} onChange={this.handleDeviceNameChange}/>
    );
  }

}

export default DeviceNameScene;
