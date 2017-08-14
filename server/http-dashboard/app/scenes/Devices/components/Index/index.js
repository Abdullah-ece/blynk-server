import React from 'react';
import DevicesList from './../DevicesList';
import PageLayout from 'components/PageLayout';
import DevicesSearch from './../DevicesSearch';
import DevicesToolbar from './../DevicesToolbar';
import {
  DeviceDetails as DeviceDetailsScene
} from 'scenes/Devices/scenes';
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

    devicesSortValue: React.PropTypes.string,
    devicesSortChange: React.PropTypes.func,
  };

  shouldComponentUpdate(nextProps) {
    return !(_.isEqual(nextProps.devicesSortValue, this.props.devicesSortValue)) || !(_.isEqual(nextProps.devices, this.props.devices)) || this.props.params.id !== nextProps.params.id || this.props.location.pathname !== nextProps.location.pathname;
  }

  handleDeviceSelect(device) {
    this.context.router.push(`/devices/${device.get('id')}`);
  }

  render() {

    if (!this.props.params.id)
      return null;

    return (
      <PageLayout>
        <PageLayout.Navigation>
          <DevicesSearch devicesSortValue={this.props.devicesSortValue}
                         devicesSortChange={this.props.devicesSortChange}/>
          <DevicesToolbar location={this.props.location} params={this.props.params}/>
          <DevicesList devices={this.props.devices} activeId={Number(this.props.params.id)}
                       onDeviceSelect={this.handleDeviceSelect.bind(this)}/>
        </PageLayout.Navigation>
        <PageLayout.Content>
          <DeviceDetailsScene params={this.props.params} location={this.props.location}/>
        </PageLayout.Content>
      </PageLayout>
    );
  }

}

export default Index;
