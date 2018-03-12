import React from 'react';
import {
  DevicesToolbar
} from "scenes/Devices/components";
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {DeviceSmartSearchState} from 'data/Storage/actions';
import {DevicesListFilterValueChange} from 'data/Devices/actions';
import PropTypes from 'prop-types';
import {DEVICES_FILTERS} from 'services/Devices';

@connect((state) => ({
  smartSearch: state.Storage.deviceSmartSearch,
  devicesFilter: state.Devices.devicesListFilterValue,
}), (dispatch) => ({
  changeSmartSearch: bindActionCreators(DeviceSmartSearchState, dispatch),
  changeFilter: bindActionCreators(DevicesListFilterValueChange, dispatch)
}))
class DevicesToolbarScene extends React.Component {

  static propTypes = {
    changeFilter: PropTypes.func,
    changeSmartSearch: PropTypes.func,

    smartSearch: PropTypes.bool,

    location: PropTypes.object,
    params: PropTypes.object,

    devicesFilter: PropTypes.oneOf([
      DEVICES_FILTERS.ALL_DEVICES,
      DEVICES_FILTERS.BY_PRODUCT,
      DEVICES_FILTERS.BY_LOCATION,
    ])
  };

  constructor(props) {
    super(props);

    this.handleDevicesFilterChange = this.handleDevicesFilterChange.bind(this);
    this.handleSmartSearchEnableChange = this.handleSmartSearchEnableChange.bind(this);
  }

  componentWillUnmount() {
    this.props.changeFilter(DEVICES_FILTERS.ALL_DEVICES);
  }

  handleDevicesFilterChange(value) {
    this.props.changeFilter(value);
  }

  handleSmartSearchEnableChange(value) {
    this.props.changeSmartSearch(value);
  }

  render() {

    const {
      smartSearch,
      location,
      params,
      devicesFilter
    } = this.props;

    return (
      <DevicesToolbar location={location}
                      params={params}
                      devicesFilter={devicesFilter}
                      onDevicesFilterChange={this.handleDevicesFilterChange}
                      smartSearch={smartSearch}
                      onSmartSearchChange={this.handleSmartSearchEnableChange}/>
    );
  }

}

export default DevicesToolbarScene;
