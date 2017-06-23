import React from "react";
import {Tabs} from "antd";
import {Dashboard, DeviceInfo, Timeline} from "./components";
import _ from "lodash";
import "./styles.less";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import {DeviceUpdate} from "data/Devices/api";
import {ContentEditable} from 'components';

@connect((state) => ({
  account: state.Account
}), (dispatch) => ({
  updateDevice: bindActionCreators(DeviceUpdate, dispatch)
}))
class Device extends React.Component {

  static propTypes = {
    device: React.PropTypes.object,
    account: React.PropTypes.object,
    params: React.PropTypes.object,
    location: React.PropTypes.object,
    onChange: React.PropTypes.func,
    updateDevice: React.PropTypes.func
  };

  shouldComponentUpdate(nextProps) {
    return !(_.isEqual(nextProps.device, this.props.device)) ||
      !(_.isEqual(nextProps.location, this.props.location));
  }

  onDeviceChange(device) {
    return this.props.updateDevice({
      orgId: this.props.account.orgId
    }, device);
  }

  handleDeviceNameChange(value) {
    this.onDeviceChange(this.props.device.set('name', value));
  }

  render() {
    const TabPane = Tabs.TabPane;
    return (
      <div className="devices--device">
        <div className="devices--device-name">
          <ContentEditable value={this.props.device.get('name')} onChange={this.handleDeviceNameChange.bind(this)}/>
        </div>
        <Tabs defaultActiveKey="2" className="page-layout-tabs-navigation">
          <TabPane tab="Dashboard" key="1">
            <div className="devices-device-tab-inner">
              <Dashboard />
            </div>
          </TabPane>
          <TabPane tab="Timeline" key="2">
            <Timeline params={this.props.params} location={this.props.location}/>
          </TabPane>
          <TabPane tab="Device Info" key="3">
            <DeviceInfo onChange={this.onDeviceChange.bind(this)} device={this.props.device}/>
          </TabPane>
          <TabPane tab="Labels" key="4">
            <div style={{padding: '12px 0'}}>Labels</div>
          </TabPane>
        </Tabs>
      </div>
    );
  }

}

export default Device;
