import React from 'react';
import DeviceItem from '../DeviceItem';
import PropTypes from 'prop-types';
import _ from 'lodash';

class AllDevices extends React.Component {

  static propTypes = {
    devices: PropTypes.array,

    activeDeviceId    : PropTypes.number,
    handleDeviceSelect: PropTypes.func,
  };

  shouldComponentUpdate(nextProps) {
    return (
      !_.isEqual(nextProps.devices, this.props.devices) ||
      !_.isEqual(nextProps.activeDeviceId, this.props.activeDeviceId)
    );
  }

  render() {

    const {activeDeviceId} = this.props;

    return (
      <div className="navigation-devices-list">
        {this.props.devices && this.props.devices.map((device) => (
          <DeviceItem key={device.id}
                      device={device}
                      active={device.id === activeDeviceId}
                      onClick={this.props.handleDeviceSelect}
          />
        ))}
      </div>
    );
  }

}

export default AllDevices;
