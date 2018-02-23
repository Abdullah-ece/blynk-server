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
  FILTERED_DEVICES_SORT,
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
  organization: state.Organization,
  smartSearch: state.Storage.deviceSmartSearch
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

    organization: React.PropTypes.object,
    smartSearch: React.PropTypes.bool
  };

  static getLocationName(device) {
    if (device && device.get('metaFields')) {
      const index = device.get('metaFields').findIndex((field) => field.get('name') === hardcodedRequiredMetadataFieldsNames.LocationName);

      if (index === -1) {
        return false;
      } else {
        return device.getIn(['metaFields', index, 'value']);
      }
    }
    return false;
  }

  static getProductName(device) {
    if (device.get('productName')) {
      return device.get('productName');
    } else {
      return false;
    }
  }

  constructor(props) {
    super(props);

    this.devicesSortChange = this.devicesSortChange.bind(this);
    this.handleFilterChange = this.handleFilterChange.bind(this);
  }

  componentWillMount() {
    this.redirectToFirstDeviceIfIdParameterMissed();

    // empty tags if they are still in the store from the previous search
    this.props.changeForm(DEVICES_SEARCH_FORM_NAME, 'tags', []);
  }

  componentWillUpdate(nextProps) {

    const sortingUnavailableOnSpecificFilters = [
      DEVICES_SORT.DATE_ADDED_ASC.key,
      DEVICES_SORT.DATE_ADDED_DESC.key,
      DEVICES_SORT.LAST_REPORTED_ASC.key,
      DEVICES_SORT.LAST_REPORTED_DESC.key,
    ];

    const specificFilters = [
      DEVICES_FILTERS.BY_PRODUCT,
      DEVICES_FILTERS.BY_LOCATION,
    ];

    const devicesFilterValue = nextProps.devicesFilterFormValues.get('filter');

    if (devicesFilterValue && nextProps.devicesSortValue && specificFilters.indexOf(devicesFilterValue) !== -1 && sortingUnavailableOnSpecificFilters.indexOf(nextProps.devicesSortValue) !== -1) {
      nextProps.devicesSortChange(DEVICES_SORT.REQUIRE_ATTENTION.key);
    }
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

  sortDevicesMap(devices, sort) {
    if (!FILTERED_DEVICES_SORT[sort])
      return null;

    devices = devices.sort((a, b) => FILTERED_DEVICES_SORT[sort].compare(a, b));

    return devices.map(device => device.set('items', this.sortDevicesList(device.get('items'), sort)));
  }

  sortDevicesList(devices, sort) {
    return devices.sort((a, b) => DEVICES_SORT[sort].compare(a, b));
  }

  sortDevicesBasedOnFilter(devices, sort, filter) {

    if (filter === DEVICES_FILTERS.ALL_DEVICES)
      return this.sortDevicesList(devices, sort);

    if (filter === DEVICES_FILTERS.BY_LOCATION || filter === DEVICES_FILTERS.BY_PRODUCT)
      return this.sortDevicesMap(devices, sort);

  }

  getDevicesList() {
    const { devices, devicesSearchFormValues, smartSearch } = this.props;

    if (smartSearch){
      // search by smart tags
      const tags = devicesSearchFormValues.get('tags');

      if (!tags || tags.size === 0){
        return devices;
      }

      const _tags = tags.toJS();
      const deviceIds = _.intersection(..._tags.map(t => t.devices));

      return devices.filter(d => deviceIds.indexOf(d.get('id')) !== -1);
    } else {
      // search by query
      const nameToSearch = (devicesSearchFormValues.get('name') || '').trim().toLowerCase();
  
      if (nameToSearch) {
        return devices.filter(device =>
          device.get('name').toLowerCase().indexOf(nameToSearch) !== -1
        );
      }
    }

    return devices;
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

  applyAllDevicesFilter(devices) {
    return devices;
  }

  applyByLocationFilter(devices) {
    return this.applyFilterForDevices(devices, Devices.getLocationName);
  }

  applyByProductFilter(devices) {
    return this.applyFilterForDevices(devices, Devices.getProductName);
  }

  applyDevicesFilter(type, devices) {
    if (type === DEVICES_FILTERS.ALL_DEVICES)
      return this.applyAllDevicesFilter(devices);

    if (type === DEVICES_FILTERS.BY_LOCATION)
      return this.applyByLocationFilter(devices);

    if (type === DEVICES_FILTERS.BY_PRODUCT)
      return this.applyByProductFilter(devices);

  }

  render() {

    if (!this.props.devices.size) {
      return (
        <NoDevices isAnyProductExist={!!this.props.products.length}
                   location={this.props.location}
                   params={this.props.params}
                   organization={this.props.organization}/>);
    } else {

      const devicesFilterValue = this.props.devicesFilterFormValues.get('filter');

      let devices = this.getDevicesList();

      devices = this.applyDevicesFilter(devicesFilterValue, devices);

      devices = this.sortDevicesBasedOnFilter(devices, this.props.devicesSortValue, devicesFilterValue);

      return (<Index filterValue={devicesFilterValue}
                     devicesSearchValue={this.props.devicesSearchFormValues.get('name') || ''}
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
