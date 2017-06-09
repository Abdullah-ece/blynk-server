import React from 'react';
import {Tabs} from 'antd';
import {Dashboard, DeviceInfo} from './components';
import _ from 'lodash';
import './styles.less';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {DeviceUpdate} from 'data/Devices/api';

@connect(() => ({}), (dispatch) => ({
  updateDevice: bindActionCreators(DeviceUpdate, dispatch)
}))
class Device extends React.Component {

  static propTypes = {
    device: React.PropTypes.object,
    onChange: React.PropTypes.func,
    updateDevice: React.PropTypes.func
  };

  shouldComponentUpdate(nextProps) {
    return !(_.isEqual(nextProps.device, this.props.device));
  }

  onDeviceChange(device) {
    return this.props.updateDevice(device);
  }

  render() {
    const TabPane = Tabs.TabPane;
    return (
      <Tabs defaultActiveKey="3" className="page-layout-tabs-navigation">
        <TabPane tab="Dashboard" key="1">
          <div className="devices-device-tab-inner">
            <Dashboard />
          </div>
        </TabPane>
        <TabPane tab="Events Log" key="2">
          <div style={{padding: '12px 0'}}>Events Log</div>
        </TabPane>
        <TabPane tab="Device Info" key="3">
          <DeviceInfo onChange={this.onDeviceChange.bind(this)} device={this.props.device}/>
        </TabPane>
        <TabPane tab="Labels" key="4">
          <div style={{padding: '12px 0'}}>Labels</div>
        </TabPane>
      </Tabs>

    );
  }

}

export default Device;
