import React from 'react';
import DevicesList from 'scenes/Devices/components/DevicesList';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {
  DEVICES_FILTERS,
  DEVICES_SEARCH_FORM_NAME
} from 'services/Devices';
import {getFormValues} from 'redux-form';

@connect((state) => ({
  devices                : state.Devices.devices,
  devicesFilterValue     : state.Devices.devicesListFilterValue,
  smartSearch            : state.Storage.deviceSmartSearch,
  devicesSearchFormValues: getFormValues(DEVICES_SEARCH_FORM_NAME)(state) || {},
  devicesSortValue       : state.Devices.devicesListSorting.value,
}))
class DevicesListScene extends React.Component {

  static propTypes = {
    devices       : PropTypes.arrayOf(
      PropTypes.shape({
        id  : PropTypes.number,
        name: PropTypes.string,
      })
    ),
    smartSearch   : PropTypes.bool,
    activeDeviceId: PropTypes.number,

    devicesSearchFormValues: PropTypes.object,

    devicesSortValue: PropTypes.string,
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
      devicesFilterValue,
      devicesSearchFormValues,
      devicesSortValue
    } = this.props;

    return (
      <DevicesList devicesSortValue={devicesSortValue}
                   devicesSearchFormValues={devicesSearchFormValues}
                   devicesFilterValue={devicesFilterValue}
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
