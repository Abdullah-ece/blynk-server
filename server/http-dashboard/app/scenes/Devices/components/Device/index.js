import React from 'react';
import {Tabs} from 'antd';
import {Dashboard, DeviceInfo} from './components';
import './styles.less';

class Device extends React.Component {

  render() {
    const TabPane = Tabs.TabPane;
    return (
      <Tabs defaultActiveKey="1" className="page-layout-tabs-navigation">
        <TabPane tab="Dashboard" key="1">
          <div className="devices-device-tab-inner">
            <Dashboard />
          </div>
        </TabPane>
        <TabPane tab="Events Log" key="2">
          <div style={{padding: '12px 0'}}>Events Log</div>
        </TabPane>
        <TabPane tab="Device Info" key="3">
          <DeviceInfo />
        </TabPane>
        <TabPane tab="Labels" key="4">
          <div style={{padding: '12px 0'}}>Labels</div>
        </TabPane>
      </Tabs>

    );
  }

}

export default Device;
