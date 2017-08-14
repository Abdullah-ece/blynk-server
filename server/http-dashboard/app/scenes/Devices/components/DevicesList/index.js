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

  // track scroll to display go top button
  // componentWillUpdate() {
  // const node = ReactDOM.findDOMNode(this);
  // console.log(node.scrollTop, node.scrollHeight, (node));
  // }

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
        { this.props.devices && !this.props.devices.size && (
          <p>No any device found</p>
        )}
      </div>
    );
  }

}

export default DevicesList;
