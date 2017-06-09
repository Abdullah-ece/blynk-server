import React from 'react';
import {Button, Tooltip} from 'antd';
import DeviceCreateModal from '../DeviceCreateModal';
import './styles.less';

class DevicesToolbar extends React.Component {

  state = {
    isDeviceCreateModalVisible: false
  };

  onDeviceCreateModalClose() {
    this.setState({
      isDeviceCreateModalVisible: false
    });
  }

  handleDeviceCreateClick() {
    this.setState({
      isDeviceCreateModalVisible: true
    });
  }

  render() {
    return (
      <div className="devices--toolbar">
        <Tooltip placement="bottomRight" title="Create new device">
          <Button icon="plus-square-o" size="small" onClick={this.handleDeviceCreateClick.bind(this)}/>
        </Tooltip>
        <Tooltip placement="bottomRight" title="Device Location">
          <Button icon="global" size="small" disabled={true}/>
        </Tooltip>
        <Tooltip placement="bottomRight" title="Device Owner">
          <Button icon="user" size="small" disabled={true}/>
        </Tooltip>
        <Tooltip placement="bottom" title="Global Filter">
          <Button icon="filter" size="small" disabled={true}/>
        </Tooltip>

        <DeviceCreateModal visible={this.state.isDeviceCreateModalVisible}
                           onClose={this.onDeviceCreateModalClose.bind(this)}/>

      </div>
    );
  }

}

export default DevicesToolbar;
