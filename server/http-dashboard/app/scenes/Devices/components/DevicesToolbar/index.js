import React from 'react';
import {Button, Tooltip} from 'antd';
import {DeviceCreateModal} from 'scenes/Devices/scenes';
import {
  DEVICES_FILTERS,
} from 'services/Devices';
import './styles.less';
import PropTypes from 'prop-types';
import { VerifyPermission, PERMISSIONS_INDEX } from "services/Roles";

class DevicesToolbar extends React.Component {

  static contextTypes = {
    router: PropTypes.object,
  };

  static propTypes = {
    devicesFilter: PropTypes.string,

    onSmartSearchChange: PropTypes.func,
    onDevicesFilterChange: PropTypes.func,

    location: PropTypes.object,
    params  : PropTypes.object,

    smartSearch: PropTypes.bool,
    permissions: React.PropTypes.number,
  };

  constructor(props) {
    super(props);

    // this.handleAllDevicesSelect = this.handleFilterSelect.bind(this, DEVICES_FILTERS.ALL_DEVICES);
    // this.handleByLocationSelect = this.handleFilterSelect.bind(this, DEVICES_FILTERS.BY_LOCATION);
    // this.handleByProductSelect = this.handleFilterSelect.bind(this, DEVICES_FILTERS.BY_PRODUCT);

    this.displayListOfDevices = this.displayListOfDevices.bind(this);
    this.filterDevicesByProduct = this.filterDevicesByProduct.bind(this);
    this.filterDevicesByLocation = this.filterDevicesByLocation.bind(this);

    this.handleSmartSearchChange = this.handleSmartSearchChange.bind(this);
    this.handleDeviceCreateModalClose = this.handleDeviceCreateModalClose.bind(this);
  }

  state = {
    isDeviceCreateModalVisible: false
  };

  componentWillMount() {
    this.checkModalVisibility();
  }

  shouldComponentUpdate(nextProps, nextState) {
    return (
      this.props.devicesFilter !== nextProps.devicesFilter ||
      this.props.location.pathname !== nextProps.location.pathname ||
      this.props.smartSearch !== nextProps.smartSearch || // super fuzzy logic
                                                          // don't forget to add your props here
      this.state.isDeviceCreateModalVisible !== nextState.isDeviceCreateModalVisible
    );
  }

  componentDidUpdate() {
    this.checkModalVisibility();
  }

  handleSmartSearchChange() {
    if(typeof this.props.onSmartSearchChange === 'function') {
      return this.props.onSmartSearchChange(!this.props.smartSearch);
    }

    return false;
  }

  handleDeviceCreateModalClose() {
    this.context.router.push(`/devices/${this.props.params.id}`);
  }

  handleDeviceCreateClick() {
    this.context.router.push(`/devices/${this.props.params.id}/create`);
  }

  checkModalVisibility() {
    if (this.props.location.pathname.indexOf('create') !== -1 && !this.state.isDeviceCreateModalVisible) {
      this.setState({
        isDeviceCreateModalVisible: true
      });
    } else if (this.props.location.pathname.indexOf('create') === -1 && this.state.isDeviceCreateModalVisible) {
      this.setState({
        isDeviceCreateModalVisible: false
      });
    }
  }

  handleDevicesFilterChange(value) {
    if(typeof this.props.onDevicesFilterChange === 'function')
      this.props.onDevicesFilterChange(value);
  }

  filterDevicesByLocation() {
    this.handleDevicesFilterChange(DEVICES_FILTERS.BY_LOCATION);
  }

  filterDevicesByProduct() {
    this.handleDevicesFilterChange(DEVICES_FILTERS.BY_PRODUCT);
  }

  displayListOfDevices() {
    this.handleDevicesFilterChange(DEVICES_FILTERS.ALL_DEVICES);
  }

  render() {

    const {devicesFilter, smartSearch} = this.props;

    return (
      <div className="devices--toolbar">
        <Tooltip placement="topRight" title="All Devices" mouseEnterDelay={.75}>
          <Button icon="switcher" size="small"
                  onClick={this.displayListOfDevices}
                  className={devicesFilter === DEVICES_FILTERS.ALL_DEVICES ? 'active' : null}/>
        </Tooltip>
        <Tooltip placement="top" title="Filter By Location"  mouseEnterDelay={.75}>
          <Button icon="environment-o" size="small"
                  onClick={this.filterDevicesByLocation}
                  className={devicesFilter === DEVICES_FILTERS.BY_LOCATION ? 'active' : null}/>
        </Tooltip>
        <Tooltip placement="top" title="Filter By Product"  mouseEnterDelay={.75}>
          <Button icon="appstore-o" size="small"
                  onClick={this.filterDevicesByProduct}
                  className={devicesFilter === DEVICES_FILTERS.BY_PRODUCT ? 'active' : null}/>
        </Tooltip>
        <span/>
        {VerifyPermission(this.props.permissions, PERMISSIONS_INDEX.OWN_DEVICES_CREATE)
        && process.env.BLYNK_CREATE_DEVICE
        && JSON.parse(process.env.BLYNK_CREATE_DEVICE)
        && <Tooltip placement="top" title="Create new device">
          <Button icon="plus-square-o" size="small" onClick={this.handleDeviceCreateClick.bind(this)}/>
        </Tooltip>}
        <Tooltip placement="topRight" title="Smart Search" mouseEnterDelay={.75}>
          <Button icon="search" size="small"
                  onClick={this.handleSmartSearchChange}
                  className={smartSearch ? 'active' : null}/>
        </Tooltip>

        <DeviceCreateModal visible={this.state.isDeviceCreateModalVisible}
                           onClose={this.handleDeviceCreateModalClose}/>

      </div>
    );
  }

}

export default DevicesToolbar;
