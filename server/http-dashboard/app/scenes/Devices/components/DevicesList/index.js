import React from 'react';
import {DeviceItem} from './components';

import './styles.less';

class DevicesList extends React.Component {

  static propTypes = {
    devices: React.PropTypes.array,
    active: React.PropTypes.number,
    deviceKey: React.PropTypes.string
  };

  render() {

    const isActive = (key, device) => {
      if (this.props.deviceKey && device[this.props.deviceKey] === this.props.active) {
        return true;
      } else if (!this.props.deviceKey && this.props.active && key === this.props.active) {
        return true;
      }
      return false;
    };

    return (
      <div className="navigation-devices-list">
        { Array.isArray(this.props.devices) && this.props.devices.map((device, key) => (
          <DeviceItem key={device.id}
                      name={device.name}
                      product={device.product}
                      critical={device.critical}
                      warning={device.warning}
                      active={isActive(key, device)}/>
        ))}
      </div>
    );
  }

}

export default DevicesList;
