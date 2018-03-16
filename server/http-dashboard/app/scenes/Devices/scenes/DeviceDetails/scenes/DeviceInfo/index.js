import React from 'react';
import {DeviceInfo} from 'scenes/Devices/components/Device/components';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';

@connect((state) => ({
  device: state.Devices.deviceDetails
}))
class DeviceInfoScene extends React.Component {

  static propTypes = {
    device: PropTypes.object
  };

  render() {

    const {device} = this.props;

    return (
      <DeviceInfo device={device}/>
    );
  }

}

export default DeviceInfoScene;
