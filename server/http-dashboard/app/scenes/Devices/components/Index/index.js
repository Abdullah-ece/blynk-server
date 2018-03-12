import React from 'react';
import {
  DevicesList,
  DevicesToolbar,
  DevicesSearch
} from 'scenes/Devices/scenes';
import PageLayout from 'components/PageLayout';
import {
  Icon
} from 'antd';
import {
  // DEVICES_FILTERS,
  DEVICES_SORT,
} from 'services/Devices';

class Index extends React.Component {

  static propTypes = {
    location: React.PropTypes.object,
    params: React.PropTypes.object,

    redirectToDeviceId: React.PropTypes.func,
  };

  static sortingOptions = {
    STATUS: {
      key: DEVICES_SORT.REQUIRE_ATTENTION.key,
      label: <span><Icon type="arrow-down"/> Status</span>,
      text: '↓ Status'
    },
    AZ: {
      key: DEVICES_SORT.AZ.key,
      label: <span><Icon type="arrow-down"/> A-Z</span>,
      text: '↓ A-Z'
    },
    ZA: {
      key: DEVICES_SORT.ZA.key,
      label: <span><Icon type="arrow-down"/> Z-A</span>,
      text: ' ↓ Z-A'
    },
    DATE_ASC: {
      key: DEVICES_SORT.DATE_ADDED_ASC.key,
      label: <span><Icon type="arrow-down"/> Added</span>,
      text: '↓ Added'
    },
    DATE_DESC: {
      key: DEVICES_SORT.DATE_ADDED_DESC.key,
      label: <span><Icon type="arrow-up"/> Added</span>,
      text: '↑ Added'
    },
    REPORTED_ASC: {
      key: DEVICES_SORT.LAST_REPORTED_ASC.key,
      label: <span><Icon type="arrow-down"/> Reported</span>,
      text: '↓ Reported'
    },
    REPORTED_DESC: {
      key: DEVICES_SORT.LAST_REPORTED_DESC.key,
      label: <span><Icon type="arrow-up"/> Reported</span>,
      text: '↑ Reported'
    }
  };

  constructor(props) {
    super(props);

    this.handleDeviceSelect = this.handleDeviceSelect.bind(this);
  }

  handleDeviceSelect(device) {
    if(typeof this.props.redirectToDeviceId === 'function')
      this.props.redirectToDeviceId(device.id);
  }

  render() {

    if (!this.props.params.id)
      return null;

    return (
      <PageLayout>
        <PageLayout.Navigation>
          <DevicesSearch />
          <DevicesToolbar location={this.props.location} params={this.props.params}/>
          <DevicesList activeDeviceId={Number(this.props.params.id)} onDeviceSelect={this.handleDeviceSelect}/>
          {/*<DevicesSearch sortingOptions={sortingOptionsList}*/}
                         {/*devicesSortValue={this.props.devicesSortValue}*/}
                         {/*devicesSortChange={this.props.devicesSortChange}/>*/}
        </PageLayout.Navigation>
        <PageLayout.Content>
          {/*<DeviceDetailsScene params={this.props.params} location={this.props.location}/>*/}
        </PageLayout.Content>
      </PageLayout>
    );
  }

}

export default Index;
