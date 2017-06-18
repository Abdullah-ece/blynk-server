import React from 'react';
import {Index, NoDevices} from './components';
import {connect} from 'react-redux';
import {List} from "immutable";

@connect((state) => ({
  products: state.Product.products,
  devices: state.Devices.get('devices'),
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
  };

  render() {

    if (!this.props.devices.size) {
      return (
        <NoDevices isAnyProductExist={!!this.props.products.length}
                   location={this.props.location}
                   params={this.props.params}/>);
    } else {
      return (<Index devices={this.props.devices}
                     location={this.props.location}
                     products={this.props.products}
                     params={this.props.params}/>);
    }


  }

}

export default Devices;
