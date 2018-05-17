import React from 'react';
import {
  DevicesSearch
} from 'scenes/Devices/components';
import {Icon} from 'antd';

import {DEVICES_SEARCH_FORM_NAME, DEVICES_SORT} from 'services/Devices';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {getFormValues, change} from 'redux-form';
import PropTypes from 'prop-types';
import _ from 'lodash';
import {
  StorageDevicesSortChange
} from 'data/Storage/actions';
import {DEVICES_FILTERS} from "services/Devices/index";

@connect(state => ({
  smartSearch            : state.Storage.deviceSmartSearch,
  devicesSortValue       : state.Storage.devicesListSorting.value,
  devicesFilter          : state.Devices.devicesListFilterValue,
  devices                : state.Devices.devicesForSearch,
  products               : state.Product.products,
  devicesSearchFormValues: getFormValues(DEVICES_SEARCH_FORM_NAME)(state) || {},
}), dispatch => ({
  changeForm       : bindActionCreators(change, dispatch),
  devicesSortChange: bindActionCreators(StorageDevicesSortChange, dispatch)
}))
class DevicesSearchScene extends React.Component {

  static propTypes = {
    smartSearch: PropTypes.bool,

    devices : PropTypes.array,
    products: PropTypes.array,

    devicesSearchFormValues: PropTypes.object,

    devicesFilter   : PropTypes.string,
    devicesSortValue: PropTypes.string,

    changeForm       : PropTypes.func,
    devicesSortChange: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleDevicesSortChange = this.handleDevicesSortChange.bind(this);
    this.handleUpdateTags = this.handleUpdateTags.bind(this);
  }

  componentWillMount() {
    // empty tags if they are still in the store from the previous search
    this.props.changeForm(DEVICES_SEARCH_FORM_NAME, 'tags', []);
  }

  shouldComponentUpdate(nextProps) {
    return (
      !_.isEqual(nextProps.smartSearch, this.props.smartSearch) ||
      !_.isEqual(nextProps.devices, this.props.devices) ||
      !_.isEqual(nextProps.products, this.props.products) ||
      !_.isEqual(nextProps.devicesSearchFormValues, this.props.devicesSearchFormValues) ||
      !_.isEqual(nextProps.devicesFilter, this.props.devicesFilter) ||
      !_.isEqual(nextProps.devicesSortValue, this.props.devicesSortValue)
    );
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

    const devicesFilterValue = nextProps.devicesFilter;

    if (devicesFilterValue && nextProps.devicesSortValue && specificFilters.indexOf(devicesFilterValue) !== -1 && sortingUnavailableOnSpecificFilters.indexOf(nextProps.devicesSortValue) !== -1) {
      nextProps.devicesSortChange(DEVICES_SORT.REQUIRE_ATTENTION.key);
    }
  }

  componentWillUnmount() {
    this.props.devicesSortChange(DEVICES_SORT.REQUIRE_ATTENTION.key);
  }

  sortingOptions = {
    STATUS       : {
      key  : DEVICES_SORT.REQUIRE_ATTENTION.key,
      label: <span><Icon type="arrow-down"/> Status</span>,
      text : '↓ Status'
    },
    AZ           : {
      key  : DEVICES_SORT.AZ.key,
      label: <span><Icon type="arrow-down"/> A-Z</span>,
      text : '↓ A-Z'
    },
    ZA           : {
      key  : DEVICES_SORT.ZA.key,
      label: <span><Icon type="arrow-down"/> Z-A</span>,
      text : ' ↓ Z-A'
    },
    DATE_ASC     : {
      key  : DEVICES_SORT.DATE_ADDED_ASC.key,
      label: <span><Icon type="arrow-down"/> Added</span>,
      text : '↓ Added'
    },
    DATE_DESC    : {
      key  : DEVICES_SORT.DATE_ADDED_DESC.key,
      label: <span><Icon type="arrow-up"/> Added</span>,
      text : '↑ Added'
    },
    REPORTED_ASC : {
      key  : DEVICES_SORT.LAST_REPORTED_ASC.key,
      label: <span><Icon type="arrow-down"/> Reported</span>,
      text : '↓ Reported'
    },
    REPORTED_DESC: {
      key  : DEVICES_SORT.LAST_REPORTED_DESC.key,
      label: <span><Icon type="arrow-up"/> Reported</span>,
      text : '↑ Reported'
    }
  };

  sortingOptionsListAllDevices = [
    this.sortingOptions.STATUS,
    this.sortingOptions.AZ,
    this.sortingOptions.ZA,
    this.sortingOptions.DATE_ASC,
    this.sortingOptions.DATE_DESC,
    this.sortingOptions.REPORTED_ASC,
    this.sortingOptions.REPORTED_DESC,
  ];

  sortingOptionsListAdvanced = [
    this.sortingOptions.STATUS,
    this.sortingOptions.AZ,
    this.sortingOptions.ZA,
  ];

  handleUpdateTags(tags) {
    this.props.changeForm(DEVICES_SEARCH_FORM_NAME, 'tags', tags);
  }

  handleDevicesSortChange(value) {
    this.props.devicesSortChange(value);
  }

  render() {

    const {
      smartSearch,
      devices,
      products,
      devicesSearchFormValues,
      devicesSortValue,
      devicesFilter
    } = this.props;

    return (
      <DevicesSearch
        sortingOptions={devicesFilter === DEVICES_FILTERS.ALL_DEVICES ? this.sortingOptionsListAllDevices : this.sortingOptionsListAdvanced}
        devicesSortChange={this.handleDevicesSortChange}
        devicesSortValue={devicesSortValue} changeForm={this.props.changeForm} onUpdateTags={this.handleUpdateTags} smartSearch={smartSearch}
        devices={devices}
        products={products}
        devicesSearchFormValues={devicesSearchFormValues}/>
    );
  }

}

export default DevicesSearchScene;
