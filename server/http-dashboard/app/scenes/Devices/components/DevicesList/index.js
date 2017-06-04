import React from 'react';
import {DeviceItem} from './components';

import './styles.less';
import {List} from "immutable";

class DevicesList extends React.Component {

  static propTypes = {
    devices: React.PropTypes.instanceOf(List),
    activeId: React.PropTypes.number,

    onDeviceSelect: React.PropTypes.func,
  };

  handleDeviceSelect(device) {
    if (typeof this.props.onDeviceSelect === 'function') {
      this.props.onDeviceSelect(device);
    }
  }

  render() {

    const isActive = (device) => {
      return device.get('id') === this.props.activeId;
    };

    return (
      <div className="navigation-devices-list">
        { this.props.devices && this.props.devices.map((device) => (
          <DeviceItem key={device.get('id')}
                      onClick={this.handleDeviceSelect.bind(this, device)}
                      device={device}
                      active={isActive(device)}/>
        ))}
      </div>
    );
  }

}

export default DevicesList;
