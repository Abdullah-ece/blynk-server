import React from 'react';
import {
  AllDevices,
  ByLocation,
  ByProduct
} from './components';
import {DEVICES_FILTERS} from "services/Devices";

import './styles.less';
import {List, Map} from "immutable";

class DevicesList extends React.Component {

  static propTypes = {
    devices: React.PropTypes.oneOfType([
      React.PropTypes.instanceOf(Map),
      React.PropTypes.instanceOf(List),
    ]),

    activeId: React.PropTypes.number,
    type: React.PropTypes.string,
    onDeviceSelect: React.PropTypes.func,
  };

  // track scroll to display go top button
  // componentWillUpdate() {
  // const node = ReactDOM.findDOMNode(this);
  // console.log(node.scrollTop, node.scrollHeight, (node));
  // }

  constructor(props) {
    super(props);

    this.isActive = this.isActive.bind(this);
    this.handleDeviceSelect = this.handleDeviceSelect.bind(this);
  }

  handleDeviceSelect(device) {
    if (typeof this.props.onDeviceSelect === 'function') {
      this.props.onDeviceSelect(device);
    }
  }

  isActive(device) {
    return device.get('id') === this.props.activeId;
  }

  render() {

    const props = {
      isActive: this.isActive,
      devices: this.props.devices,
      handleDeviceSelect: this.handleDeviceSelect,
    };

    if (!this.props.devices || this.props.devices && !this.props.devices.size)
      return (<div className="navigation-devices-list">No any device found</div>);

    if (this.props.devices.size && !this.props.type || this.props.type && this.props.type === DEVICES_FILTERS.ALL_DEVICES)
      return (<AllDevices {...props}/>);

    if (this.props.devices.size && this.props.type && this.props.type === DEVICES_FILTERS.BY_LOCATION)
      return (<ByLocation {...props}/>);

    if (this.props.devices.size && this.props.type && this.props.type === DEVICES_FILTERS.BY_PRODUCT)
      return (<ByProduct {...props}/>);

  }

}

export default DevicesList;
