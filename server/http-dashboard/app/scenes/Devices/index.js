import React from 'react';
import PageLayout from 'components/PageLayout';
import {DevicesSearch, DevicesList, Device} from './components';
import {connect} from 'react-redux';

@connect((state) => ({
  devices: state.Devices.devices
}))
class Devices extends React.Component {

  static propTypes = {
    devices: React.PropTypes.array
  };

  render() {

    return (
      <PageLayout>
        <PageLayout.Navigation>
          <DevicesSearch />
          <DevicesList devices={this.props.devices} active={0} deviceKey="id"/>
        </PageLayout.Navigation>
        <PageLayout.Content>
          <PageLayout.Content.Header title="Trenton Farm Equipment"/>
          <Device />
        </PageLayout.Content>
      </PageLayout>
    );
  }

}

export default Devices;
