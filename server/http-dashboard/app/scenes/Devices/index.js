import React from 'react';
import PageLayout from 'components/PageLayout';
import {DevicesSearch, DevicesList, Device} from './components';
import {connect} from 'react-redux';
import _ from 'lodash';

@connect((state) => ({
  devices: state.Devices.devices
}))
class Devices extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    devices: React.PropTypes.array,
    params: React.PropTypes.object
  };

  componentWillMount() {

    const getFirstDeviceId = () => {
      return this.props.devices.length && this.props.devices[0].id;
    };

    if (isNaN(this.props.params.id) && this.props.devices.length) {
      this.context.router.push('/devices/' + getFirstDeviceId());
    }
  }

  shouldComponentUpdate(nextProps) {
    return !(_.isEqual(nextProps.devices, this.props.devices)) || this.props.params.id !== nextProps.params.id;
  }

  handleDeviceSelect(device) {
    this.context.router.push(`/devices/${device.id}`);
  }

  getDeviceById(id) {
    return _.find(this.props.devices, {id: Number(id)});
  }

  render() {

    const selectedDevice = this.getDeviceById(this.props.params.id);

    return (
      <PageLayout>
        <PageLayout.Navigation>
          <DevicesSearch />
          <DevicesList devices={this.props.devices} active={Number(this.props.params.id)} deviceKey="id"
                       onDeviceSelect={this.handleDeviceSelect.bind(this)}/>
        </PageLayout.Navigation>
        <PageLayout.Content>
          <PageLayout.Content.Header title={selectedDevice.name}/>
          <Device device={selectedDevice}/>
        </PageLayout.Content>
      </PageLayout>
    );
  }

}

export default Devices;
