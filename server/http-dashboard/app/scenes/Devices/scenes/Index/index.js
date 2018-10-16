import React from 'react';
import {Index, NoDevices} from './../../components';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {DEVICES_SORT,TABS} from 'services/Devices';
import PropTypes from 'prop-types';
import {blynkWsSetTrackDeviceId} from 'store/blynk-websocket-middleware/actions';
import {DeviceDelete, DevicesFetch} from 'data/Devices/api';

@connect((state) => ({
  productsCount: state.Product.products && state.Product.products.length,
  devices: state.Devices.devices,
  organization: state.Organization,
}), (dispatch) => ({
  fetchDevices: bindActionCreators(DevicesFetch, dispatch),
  deviceDelete: bindActionCreators(DeviceDelete, dispatch),
  blynkWsSetTrackDeviceId: bindActionCreators(blynkWsSetTrackDeviceId, dispatch)
}))
class Devices extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object,
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

    blynkWsSetTrackDeviceId: PropTypes.func,

    deviceDelete: PropTypes.func,
    fetchDevices: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.redirectToDeviceId = this.redirectToDeviceId.bind(this);
    this.handleDeviceDelete = this.handleDeviceDelete.bind(this);
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
    this.props.blynkWsSetTrackDeviceId(-1);
  }

  redirectToDeviceId(deviceId) {
    this.props.blynkWsSetTrackDeviceId(deviceId || -1);
    const tab = this.context.router.params.tab || TABS.DASHBOARD;
    this.context.router.push('/devices/' + deviceId + "/" + tab);
  }

  redirectToFirstDeviceIfIdParameterMissed() {
    if (isNaN(Number(this.props.params.id)) && this.props.devices.length) {
      // sort devices by status because it's default sort and params.id can be missed
      // only when user loads page first time
      this.redirectToDeviceId(this.sortDevicesByStatus(this.props.devices)[0].id);
    }
  }

  handleDeviceDelete(deviceId) {

    const devices = this.props.devices.filter((device) => device.id !== deviceId);

    const sortedDevices = this.sortDevicesByStatus(devices);

    if(sortedDevices.length && sortedDevices[0] && sortedDevices[0].id) {
      this.redirectToDeviceId(sortedDevices[0].id);
    }
    this.props.deviceDelete(deviceId, this.props.organization.id).then(() => {
      this.props.fetchDevices({orgId: this.props.organization.id});
    });

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
                     onDeviceDelete={this.handleDeviceDelete}
                     location={this.props.location}
                     params={this.props.params}/>);
    }


  }

}

export default Devices;
