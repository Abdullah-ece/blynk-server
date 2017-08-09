import React from 'react';
import {Index, NoDevices} from './components';
import {
  DevicesSortChange
} from 'data/Devices/actions';
import {
  DEVICES_SORT
} from 'services/Devices';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {List} from "immutable";

@connect((state) => ({
  products: state.Product.products,
  devices: state.Devices.get('devices'),
  devicesSortValue: state.Devices.getIn(['sorting', 'value']),
}), (dispatch) => ({
  devicesSortChange: bindActionCreators(DevicesSortChange, dispatch)
}))
class Devices extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    devices: React.PropTypes.instanceOf(List),
    products: React.PropTypes.array,
    location: React.PropTypes.object,
    params: React.PropTypes.object,

    devicesSortValue: React.PropTypes.string,
    devicesSortChange: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.devicesSortChange = this.devicesSortChange.bind(this);
  }

  componentWillUnmount() {
    this.props.devicesSortChange(DEVICES_SORT.REQUIRE_ATTENTION.key);
  }

  devicesSortChange(value) {
    this.props.devicesSortChange(value);
  }

  render() {

    if (!this.props.devices.size) {
      return (
        <NoDevices isAnyProductExist={!!this.props.products.length}
                   location={this.props.location}
                   params={this.props.params}/>);
    } else {

      let devices = this.props.devices.sort((a, b) => DEVICES_SORT[this.props.devicesSortValue].compare(a, b));

      return (<Index devicesSortValue={this.props.devicesSortValue}
                     devicesSortChange={this.devicesSortChange}
                     devices={devices}
                     location={this.props.location}
                     products={this.props.products}
                     params={this.props.params}/>);
    }


  }

}

export default Devices;
