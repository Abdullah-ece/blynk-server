import React from 'react';
import DevicesList from 'scenes/Devices/components/DevicesList';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';

@connect((state) => ({
  devices    : state.Devices.devices,
  smartSearch: state.Storage.deviceSmartSearch,
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
    } = this.props;

    return (
      <DevicesList onDeviceSelect={this.handleDeviceSelect} devices={devices} smartSearch={smartSearch}
                   activeId={activeDeviceId} devicesSearchValue={devicesSearchValue}/>
    );
  }

}

export default DevicesListScene;
