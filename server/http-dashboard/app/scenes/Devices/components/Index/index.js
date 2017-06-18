import React from 'react';
import Device from './../Device';
import DevicesList from './../DevicesList';
import PageLayout from 'components/PageLayout';
import DevicesSearch from './../DevicesSearch';
import DevicesToolbar from './../DevicesToolbar';
import _ from 'lodash';
import {List} from "immutable";

class Index extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    devices: React.PropTypes.instanceOf(List),
    products: React.PropTypes.array,
    location: React.PropTypes.object,
    params: React.PropTypes.object,
  };


  componentWillMount() {
    this.redirectToFirstDeviceIfIdParameterMissed();
  }

  shouldComponentUpdate(nextProps) {
    return !(_.isEqual(nextProps.devices, this.props.devices)) || this.props.params.id !== nextProps.params.id || this.props.location.pathname !== nextProps.location.pathname;
  }

  componentDidUpdate() {
    this.redirectToFirstDeviceIfIdParameterMissed();
  }

  redirectToFirstDeviceIfIdParameterMissed() {
    if (isNaN(Number(this.props.params.id)) || !this.getDeviceById(this.props.params.id)) {
      this.context.router.push('/devices/' + this.props.devices.first().get('id'));
    }
  }

  handleDeviceSelect(device) {
    this.context.router.push(`/devices/${device.get('id')}`);
  }

  getDeviceById(id) {
    return this.props.devices.find(device => Number(device.get('id')) === Number(id));
  }

  render() {

    const selectedDevice = this.getDeviceById(this.props.params.id);

    if (!this.props.params.id || !selectedDevice)
      return null;

    return (
      <PageLayout>
        <PageLayout.Navigation>
          <DevicesSearch />
          <DevicesToolbar location={this.props.location} params={this.props.params}/>
          <DevicesList devices={this.props.devices} activeId={Number(this.props.params.id)}
                       onDeviceSelect={this.handleDeviceSelect.bind(this)}/>
        </PageLayout.Navigation>
        <PageLayout.Content>
          <Device device={selectedDevice} params={this.props.params}/>
        </PageLayout.Content>
      </PageLayout>
    );
  }

}

export default Index;
