import React from 'react';
import PageLayout from 'components/PageLayout';
import {DevicesSearch, DevicesList} from './components';

class Devices extends React.Component {

  render() {

    const devices = [{
      id: 1,
      name: 'Trenton Farm Equipment',
      productName: 'MultiFlow FX564',
      warning: 3
    }, {
      id: 2,
      name: 'Trenton Farm Equipment',
      productName: 'MultiFlow FX564',
      critical: 5
    }, {
      id: 3,
      name: 'Trenton Farm Equipment',
      productName: 'MultiFlow FX564'
    }, {
      id: 4,
      name: 'Trenton Farm Equipment',
      productName: 'MultiFlow FX564'
    }, {
      id: 5,
      name: 'Trenton Farm Equipment',
      productName: 'MultiFlow FX564',
      critical: 3,
      warning: 2
    }, {
      id: 6,
      name: 'Trenton Farm Equipment',
      productName: 'MultiFlow FX564'
    }, {
      id: 7,
      name: 'Trenton Farm Equipment',
      productName: 'MultiFlow FX564'
    }, {
      id: 8,
      name: 'Trenton Farm Equipment',
      productName: 'MultiFlow FX564'
    }, {
      id: 9,
      name: 'Trenton Farm Equipment',
      productName: 'MultiFlow FX564'
    }];

    return (
      <PageLayout>
        <PageLayout.Navigation>
          <DevicesSearch />
          <DevicesList devices={devices} active={1} deviceKey="id"/>
        </PageLayout.Navigation>
        <PageLayout.Content>
          Content
        </PageLayout.Content>
      </PageLayout>
    );
  }

}

export default Devices;
