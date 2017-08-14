import React from 'react';
import _ from 'lodash';
import {Index, NoDevices} from './components';
import {
  DevicesSortChange
} from 'data/Devices/actions';
import {
  hardcodedRequiredMetadataFieldsNames
} from 'services/Products';
import {
  DEVICES_SORT,
  DEVICES_SEARCH_FORM_NAME,
  DEVICES_FILTER_FORM_NAME,
  DEVICES_FILTERS,
} from 'services/Devices';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {
  getFormValues,
  change,
} from 'redux-form';
import {List, fromJS, Map} from "immutable";

@connect((state) => ({
  products: state.Product.products,
  devices: state.Devices.get('devices'),
  devicesSortValue: state.Devices.getIn(['sorting', 'value']),
  devicesSearchFormValues: fromJS(getFormValues(DEVICES_SEARCH_FORM_NAME)(state) || {}),
  devicesFilterFormValues: fromJS(getFormValues(DEVICES_FILTER_FORM_NAME)(state) || {filter: DEVICES_FILTERS.DEFAULT}),
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch),
  devicesSortChange: bindActionCreators(DevicesSortChange, dispatch)
}))
class Devices extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    devices: React.PropTypes.instanceOf(List),
    devicesSearchFormValues: React.PropTypes.instanceOf(Map),
    devicesFilterFormValues: React.PropTypes.instanceOf(Map),
    products: React.PropTypes.array,
    location: React.PropTypes.object,
    params: React.PropTypes.object,

    devicesSortValue: React.PropTypes.string,
    changeForm: React.PropTypes.func,
    devicesSortChange: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.devicesSortChange = this.devicesSortChange.bind(this);
    this.handleFilterChange = this.handleFilterChange.bind(this);
  }

  componentWillMount() {
    this.redirectToFirstDeviceIfIdParameterMissed();
  }

  componentDidUpdate() {
    this.redirectToFirstDeviceIfIdParameterMissed();
  }

  componentWillUnmount() {
    this.props.devicesSortChange(DEVICES_SORT.REQUIRE_ATTENTION.key);
  }

  devicesSortChange(value) {
    this.props.devicesSortChange(value);
  }

  handleFilterChange(value) {
    this.props.changeForm(DEVICES_FILTER_FORM_NAME, 'filter', value);
  }

  getDeviceById(id) {
    return this.props.devices.find(device => Number(device.get('id')) === Number(id));
  }

  redirectToFirstDeviceIfIdParameterMissed() {
    if (isNaN(Number(this.props.params.id)) && this.getDevicesList().size) {
      this.context.router.push('/devices/' + this.getDevicesList().first().get('id'));
    }
  }

  getDevicesList() {
    let devices = this.props.devices.sort((a, b) => DEVICES_SORT[this.props.devicesSortValue].compare(a, b));

    if (this.props.devicesSearchFormValues.get('name')) {
      devices = devices.filter(device => device.get('name').trim().toLowerCase().indexOf(this.props.devicesSearchFormValues.get('name').trim().toLowerCase()) !== -1);
    }

    return devices;
  }

  applyAllDevicesFilter(devices) {
    return devices;
  }

  applyByLocationFilter(devices) {

    const getLocationName = (device) => {
      if (device && device.get('metaFields')) {
        const index = device.get('metaFields').findIndex((field) => field.get('name') === hardcodedRequiredMetadataFieldsNames.LocationName);

        if (index === -1) {
          return false;
        } else {
          return device.getIn(['metaFields', index, 'value']);
        }
      }
      return false;
    };

    let filteredDevices = {};
    let devicesWithoutLocation = [];

    devices.forEach((device) => {
      const name = getLocationName(device);
      if (name) {
        filteredDevices[name] ? filteredDevices[name].push(device) : filteredDevices[name] = [device];
      } else {
        devicesWithoutLocation.push(device);
      }
    });

    let filteredDevicesList = [];

    _.forEach(filteredDevices, (value, key) => {
      filteredDevicesList.push({
        name: key,
        items: value
      });
    });

    filteredDevicesList.push({
      name: 'Other Devices',
      isOthers: true,
      items: devicesWithoutLocation
    });

    return fromJS(filteredDevicesList);

  }

  applyByProductFilter() {

  }

  applyDevicesFilter(type, devices) {
    if (type === DEVICES_FILTERS.ALL_DEVICES)
      return this.applyAllDevicesFilter(devices);

    if (type === DEVICES_FILTERS.BY_LOCATION)
      return this.applyByLocationFilter(devices);

    if (type === DEVICES_FILTERS.ALL_DEVICES)
      return this.applyByProductFilter(devices);

  }

  render() {

    if (!this.props.devices.size) {
      return (
        <NoDevices isAnyProductExist={!!this.props.products.length}
                   location={this.props.location}
                   params={this.props.params}/>);
    } else {

      const devicesFilterValue = this.props.devicesFilterFormValues.get('filter');

      let devices = this.getDevicesList();

      devices = this.applyDevicesFilter(devicesFilterValue, devices);

      return (<Index filterValue={devicesFilterValue}
                     onFilterChange={this.handleFilterChange}
                     devicesSortValue={this.props.devicesSortValue}
                     devicesSortChange={this.devicesSortChange}
                     devices={devices}
                     location={this.props.location}
                     products={this.props.products}
                     params={this.props.params}/>);
    }


  }

}

export default Devices;
