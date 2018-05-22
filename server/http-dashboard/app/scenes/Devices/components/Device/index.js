import React from "react";
import {Tabs} from "antd";
import {Dashboard, /*DeviceInfo,*/ Timeline} from "./components";
// import _ from "lodash";
import "./styles.less";
import {TABS} from 'services/Devices';
// import {connect} from "react-redux";
// import {bindActionCreators} from "redux";
// import {DeviceUpdate} from "data/Devices/api";
// import {ContentEditable} from 'components';
import {DeviceName, DeviceInfo} from 'scenes/Devices/scenes/DeviceDetails/scenes';

class Device extends React.Component {

  static propTypes = {
    // device: React.PropTypes.object,
    // account: React.PropTypes.object,
    params: React.PropTypes.object,
    location: React.PropTypes.object,
    productId: React.PropTypes.number,
    onTabChange: React.PropTypes.func,
    // onChange: React.PropTypes.func,
    // updateDevice: React.PropTypes.func,
    // onDeviceChange: React.PropTypes.func,
    // onMetadataChange: React.PropTypes.func,
  };

  constructor(props) {
    super(props);
    this.handleTabChange = this.handleTabChange.bind(this);
    // this.onMetadataChange = this.onMetadataChange.bind(this);
  }

  // shouldComponentUpdate(/*nextProps*/) {
    // return !(_.isEqual(nextProps.device, this.props.device)) ||
    //   !(_.isEqual(nextProps.location, this.props.location));
  // }

  // onMetadataChange(metadata) {
  //   return this.props.onMetadataChange(metadata);
  // }

  // onDeviceChange(device) {
  //   return this.props.onDeviceChange(device);
  // }

  // handleDeviceNameChange(value) {
  //   this.onDeviceChange(this.props.device.set('name', value));
  // }
  handleTabChange(newTab) {
    this.props.onTabChange(newTab);
  }
  render() {

    const TabPane = Tabs.TabPane;

    return (
      <div className="devices--device">
        <div className="devices--device-name">
          <DeviceName/>
        </div>
        <Tabs defaultActiveKey={this.props.params.tab && this.props.params.tab || "Dashboard"} className="page-layout-tabs-navigation" onChange={this.handleTabChange}>
          <TabPane tab="Dashboard" key={TABS.DASHBOARD}>
            <div className="devices-device-tab-inner">
              <Dashboard params={this.props.params} productId={this.props.productId}/>
            </div>
          </TabPane>
          <TabPane tab="Timeline" key={TABS.TIMELINE}>
            <div className="devices-device-tab-inner">
              <Timeline params={this.props.params}/>
            </div>
          </TabPane>
          <TabPane tab="Device Info" key={TABS.DEVICE_INFO}>
            <div className="devices-device-tab-inner">
              <DeviceInfo params={this.props.params}/>
            </div>
          </TabPane>
        </Tabs>
      </div>
    );

    //return (
    //  <div className="devices--device" key={`devices--device${this.props.device.get('id')}`}>
    //    <div className="devices--device-name">
    //      <ContentEditable value={this.props.device.get('name')} onChange={this.handleDeviceNameChange.bind(this)}/>
    //    </div>
    //   <Tabs defaultActiveKey="1" className="page-layout-tabs-navigation">
    //      <TabPane tab="Dashboard" key="1">
    //        <div className="devices-device-tab-inner">
    //          <Dashboard params={this.props.params} dashboard={this.props.device.get('webDashboard')}/>
    //        </div>
    //      </TabPane>
    //      <TabPane tab="Timeline" key="2">
    //        <Timeline params={this.props.params} location={this.props.location}/>
    //      </TabPane>
    //      <TabPane tab="Device Info" key="3">
    //        <DeviceInfo account={this.props.account} onMetadataChange={this.onMetadataChange} device={this.props.device}/>
    //      </TabPane>
    //      {/*<TabPane tab="Labels" key="4">
    //        <div style={{padding: '12px 32px'}}>Labels</div>
    //      </TabPane>*/}
    //    </Tabs>
    //  </div>
    //);
  }

}

export default Device;
