import React from 'react';
import {
  AllDevices,
  ByLocation,
  ByProduct
} from './components';
import {DEVICES_FILTERS} from 'services/Devices';

import './styles.less';
import PropTypes from 'prop-types';

class DevicesList extends React.Component {

  static propTypes = {
    devices: PropTypes.arrayOf(
      PropTypes.shape({
        id  : PropTypes.number,
        name: PropTypes.string,
      })
    ),

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

    this.handleDeviceSelect = this.handleDeviceSelect.bind(this);
  }

  handleDeviceSelect(device) {
    if (typeof this.props.onDeviceSelect === 'function') {
      this.props.onDeviceSelect(device);
    }
  }

  render() {
    const {
      devicesSearchValue,
      smartSearch,
      devices,
      type
    } = this.props;

    const props = {
      activeDeviceId: this.props.activeId,
      devices: devices,
      handleDeviceSelect: this.handleDeviceSelect,
    };

    if (!this.props.devices || this.props.devices && !this.props.devices.length){
      const noDevicesMessage = smartSearch ? 'No devices found' : `No devices found for "${devicesSearchValue}"`;
      return (<div className="navigation-devices-list">{noDevicesMessage}</div>);
    }

    if (devices.length && !type || type && type === DEVICES_FILTERS.ALL_DEVICES)
      return (<AllDevices {...props}/>);

    if (devices.length && type && type === DEVICES_FILTERS.BY_LOCATION)
      return (<ByLocation {...props}/>);

    if (devices.length && type && type === DEVICES_FILTERS.BY_PRODUCT)
      return (<ByProduct {...props}/>);
  }
}

export default DevicesList;
