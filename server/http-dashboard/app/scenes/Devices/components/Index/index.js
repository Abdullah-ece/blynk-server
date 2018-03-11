import React from 'react';
import {
  DevicesList,
  DevicesToolbar,
  DevicesSearch
} from 'scenes/Devices/scenes';
import PageLayout from 'components/PageLayout';
import _ from 'lodash';
import {
  Icon
} from 'antd';
import {
  // DEVICES_FILTERS,
  DEVICES_SORT,
} from 'services/Devices';
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

    filterValue: React.PropTypes.string,
    devicesSortValue: React.PropTypes.string,
    devicesSearchValue: React.PropTypes.string,

    onFilterChange: React.PropTypes.func,
    devicesSortChange: React.PropTypes.func,
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

  shouldComponentUpdate(nextProps) {
    return !(_.isEqual(nextProps.devicesSearchValue, this.props.devicesSearchValue)) || !(_.isEqual(nextProps.filterValue, this.props.filterValue)) || !(_.isEqual(nextProps.devicesSortValue, this.props.devicesSortValue)) || !(_.isEqual(nextProps.devices, this.props.devices)) || this.props.params.id !== nextProps.params.id || this.props.location.pathname !== nextProps.location.pathname;
  }

  handleDeviceSelect(device) {
    this.context.router.push(`/devices/${device.id}`);
  }

  render() {

    if (!this.props.params.id)
      return null;

    // let sortingOptionsList = [];
    //
    // if(this.props.filterValue === DEVICES_FILTERS.ALL_DEVICES) {
    //   sortingOptionsList = [
    //     Index.sortingOptions.STATUS,
    //     Index.sortingOptions.AZ,
    //     Index.sortingOptions.ZA,
    //     Index.sortingOptions.DATE_ASC,
    //     Index.sortingOptions.DATE_DESC,
    //     Index.sortingOptions.REPORTED_ASC,
    //     Index.sortingOptions.REPORTED_DESC,
    //   ];
    // } else {
    //   sortingOptionsList = [
    //     Index.sortingOptions.STATUS,
    //     Index.sortingOptions.AZ,
    //     Index.sortingOptions.ZA,
    //   ];
    // }

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
