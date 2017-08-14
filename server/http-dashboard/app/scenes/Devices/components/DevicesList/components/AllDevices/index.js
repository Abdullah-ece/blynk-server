import React from 'react';
import {List} from 'immutable';
import DeviceItem from '../DeviceItem';

class AllDevices extends React.Component {

  static propTypes = {
    devices: React.PropTypes.instanceOf(List),

    isActive: React.PropTypes.func,
    handleDeviceSelect: React.PropTypes.func,
  };

  render() {
    return (
      <div className="navigation-devices-list">
        {this.props.devices && this.props.devices.map((device) => (
          <DeviceItem key={device.get('id')}
                      device={device}
                      active={this.props.isActive(device)}
                      onClick={this.props.handleDeviceSelect}
          />
        ))}
      </div>
    );
  }

}

export default AllDevices;
