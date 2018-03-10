import React from 'react';
import {
  AllDevices,
  ByLocation,
  ByProduct
} from './components';
import {DEVICES_FILTERS} from 'services/Devices';
import {
  hardcodedRequiredMetadataFieldsNames
} from 'services/Products';
import _ from 'lodash';

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
    devicesFilterValue: React.PropTypes.string,
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

  applyFilterForDevices(devices, filter) {

    let filteredDevices = {};
    let devicesWithoutLocation = [];

    devices.forEach((device) => {
      const name = filter(device);
      if (name) {
        filteredDevices[name] ? filteredDevices[name].push(device) : filteredDevices[name] = [device];
      } else {
        devicesWithoutLocation.push(device);
      }
    });

    let filteredDevicesList = [];

    _.forEach(filteredDevices, ((value, key) => {
      filteredDevicesList.push({
        name: key,
        items: value
      });
    }));

    filteredDevicesList.push({
      name: 'Other Devices',
      isOthers: true,
      items: devicesWithoutLocation
    });

    return filteredDevicesList;

  }

  getLocationName(device) {
    if (device && device.metaFields && device.metaFields.length) {
      return device.metaFields.reduce((location, item) => {
        if(String(item.name).trim() === hardcodedRequiredMetadataFieldsNames.LocationName)
          return item.value;
        return location;
      });
    }
    return false;
  }

  getProductName(device) {
    if(device && device.productName)
      return device.productName;

    return null;
  }

  applyDevicesFilter(devices, type) {
    if (type === DEVICES_FILTERS.ALL_DEVICES)
      return this.applyAllDevicesFilter(devices);

    if (type === DEVICES_FILTERS.BY_LOCATION)
      return this.applyByLocationFilter(devices);

    if (type === DEVICES_FILTERS.BY_PRODUCT)
      return this.applyByProductFilter(devices);

  }

  applyAllDevicesFilter(devices) {
    return devices;
  }

  applyByLocationFilter(devices) {
    return this.applyFilterForDevices(devices, this.getLocationName);
  }

  applyByProductFilter(devices) {
    return this.applyFilterForDevices(devices, this.getProductName);
  }

  getDevicesList() {
    const {devices} = this.props;
    return devices;
  }

  render() {
    const {
      devicesSearchValue,
      smartSearch,
      devicesFilterValue
    } = this.props;

    let devices = this.getDevicesList();

    devices = this.applyDevicesFilter(devices, devicesFilterValue);

    const props = {
      activeDeviceId: this.props.activeId,
      devices: devices,
      handleDeviceSelect: this.handleDeviceSelect,
    };

    if (!this.props.devices || this.props.devices && !this.props.devices.length){
      const noDevicesMessage = smartSearch ? 'No devices found' : `No devices found for "${devicesSearchValue}"`;
      return (<div className="navigation-devices-list">{noDevicesMessage}</div>);
    }

    if (devices.length && !devicesFilterValue || devicesFilterValue && devicesFilterValue === DEVICES_FILTERS.ALL_DEVICES)
      return (<AllDevices {...props}/>);

    if (devices.length && devicesFilterValue && devicesFilterValue === DEVICES_FILTERS.BY_LOCATION)
      return (<ByLocation {...props}/>);

    if (devices.length && devicesFilterValue && devicesFilterValue === DEVICES_FILTERS.BY_PRODUCT)
      return (<ByProduct {...props}/>);
  }
}

export default DevicesList;
