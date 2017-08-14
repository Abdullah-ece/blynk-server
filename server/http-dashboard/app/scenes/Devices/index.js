import React from 'react';
import {Index, NoDevices} from './components';
import {
  DevicesSortChange
} from 'data/Devices/actions';
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

  render() {

    if (!this.props.devices.size) {
      return (
        <NoDevices isAnyProductExist={!!this.props.products.length}
                   location={this.props.location}
                   params={this.props.params}/>);
    } else {

      let devices = this.getDevicesList();

      const devicesFilterValue = this.props.devicesFilterFormValues.get('filter');

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
