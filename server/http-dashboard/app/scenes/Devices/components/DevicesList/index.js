import React from 'react';
import {
  AllDevices,
  ByLocation,
  ByProduct
} from './components';
import {DEVICES_FILTERS} from 'services/Devices';
import {connect} from 'react-redux';

import './styles.less';
import {List, Map} from "immutable";

@connect(state => ({
  smartSearch: state.Storage.deviceSmartSearch
}))
class DevicesList extends React.Component {

  static propTypes = {
    devices: React.PropTypes.oneOfType([
      React.PropTypes.instanceOf(Map),
      React.PropTypes.instanceOf(List),
    ]),

    activeId: React.PropTypes.number,
    type: React.PropTypes.string,
    onDeviceSelect: React.PropTypes.func,
    devicesSearchValue: React.PropTypes.string,

    smartSearch: React.PropTypes.bool
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
    const {
      devicesSearchValue,
      smartSearch,
      devices,
      type
    } = this.props;

    const props = {
      isActive: this.isActive,
      devices: devices,
      handleDeviceSelect: this.handleDeviceSelect,
    };

    if (!this.props.devices || this.props.devices && !this.props.devices.size){
      const noDevicesMessage = smartSearch ? 'No devices found' : `No devices found for "${devicesSearchValue}"`;
      return (<div className="navigation-devices-list">{noDevicesMessage}</div>);
    }

    if (devices.size && !type || type && type === DEVICES_FILTERS.ALL_DEVICES)
      return (<AllDevices {...props}/>);

    if (devices.size && type && type === DEVICES_FILTERS.BY_LOCATION)
      return (<ByLocation {...props}/>);

    if (devices.size && type && type === DEVICES_FILTERS.BY_PRODUCT)
      return (<ByProduct {...props}/>);
  }
}

export default DevicesList;
