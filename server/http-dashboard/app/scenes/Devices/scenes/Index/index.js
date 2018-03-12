import React from 'react';
import {Index, NoDevices} from './../../components';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {DEVICES_SORT} from 'services/Devices';
import PropTypes from 'prop-types';
import {blynkWsSetTrackDeviceId} from 'store/blynk-websocket-middleware/actions';

@connect((state) => ({
  productsCounts: state.Product.products && state.Product.products.length,
  devices: state.Devices.devices,
  organization: state.Organization,
}), (dispatch) => ({
  blynkWsSetTrackDeviceId: bindActionCreators(blynkWsSetTrackDeviceId, dispatch)
}))
class Devices extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    devices: PropTypes.arrayOf(
      PropTypes.shape({
        id: PropTypes.number,
      })
    ),

    productsCount: PropTypes.number,

    location    : PropTypes.object,
    params      : PropTypes.object,
    organization: PropTypes.object,

    blynkWsSetTrackDeviceId: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.redirectToDeviceId = this.redirectToDeviceId.bind(this);
  }

  componentWillMount() {
    if(this.props.params.id) {
      this.props.blynkWsSetTrackDeviceId(this.props.params.id);
    }

    this.redirectToFirstDeviceIfIdParameterMissed();

  }

  componentDidUpdate() {
    this.redirectToFirstDeviceIfIdParameterMissed();
  }

  componentWillUnmount() {
    this.props.blynkWsSetTrackDeviceId(null);
  }

  redirectToDeviceId(deviceId) {
    this.props.blynkWsSetTrackDeviceId(deviceId);
    this.context.router.push('/devices/' + deviceId);
  }

  redirectToFirstDeviceIfIdParameterMissed() {
    if (isNaN(Number(this.props.params.id)) && this.props.devices.length) {
      // sort devices by status because it's default sort and params.id can be missed
      // only when user loads page first time
      this.redirectToDeviceId(this.sortDevicesByStatus(this.props.devices)[0].id);
    }
  }

  sortDevicesByStatus(devices) {
    return devices.sort((a, b) => DEVICES_SORT.REQUIRE_ATTENTION.compare(a, b));
  }

  render() {

    if (!this.props.devices.length) {
      return (
        <NoDevices isAnyProductExist={!!this.props.productsCount}
                   location={this.props.location}
                   params={this.props.params}
                   organization={this.props.organization}/>);
    } else {

      return (<Index redirectToDeviceId={this.redirectToDeviceId}
                     location={this.props.location}
                     params={this.props.params}/>);
    }


  }

}

export default Devices;
