import React from 'react';
import DevicesList from 'scenes/Devices/components/DevicesList';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {DEVICES_FILTERS} from 'services/Devices';

@connect((state) => ({
  devices           : state.Devices.devices,
  devicesFilterValue: state.Devices.devicesListFilterValue,
  smartSearch       : state.Storage.deviceSmartSearch,
}))
class DevicesListScene extends React.Component {

  static propTypes = {
    devices       : PropTypes.arrayOf(
      PropTypes.shape({
        id: PropTypes.number,
        name: PropTypes.string,
      })
    ),
    smartSearch   : PropTypes.bool,
    activeDeviceId: PropTypes.number,

    devicesSearchValue: PropTypes.string,

    onDeviceSelect: PropTypes.func,

    devicesFilterValue: PropTypes.oneOf([
      DEVICES_FILTERS.ALL_DEVICES,
      DEVICES_FILTERS.BY_PRODUCT,
      DEVICES_FILTERS.BY_LOCATION,
    ]),
  };

  constructor(props) {
    super(props);

    this.handleDeviceSelect = this.handleDeviceSelect.bind(this);
  }

  handleDeviceSelect(device) {
    if (typeof this.props.onDeviceSelect === 'function')
      this.props.onDeviceSelect(device);
  }

  render() {

    const {
      devices,
      smartSearch,
      activeDeviceId,
      devicesSearchValue,
      devicesFilterValue
    } = this.props;

    return (
      <DevicesList devicesFilterValue={devicesFilterValue}
                   onDeviceSelect={this.handleDeviceSelect}
                   devices={devices}
                   smartSearch={smartSearch}
                   activeId={activeDeviceId}
                   devicesSearchValue={devicesSearchValue}
      />
    );
  }

}

export default DevicesListScene;
