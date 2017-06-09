import React from 'react';
import {Button, Tooltip} from 'antd';
import './styles.less';

class DevicesToolbar extends React.Component {

  handleNewDeviceClick() {
    alert('Create');
  }

  render() {
    return (
      <div className="devices--toolbar">
        <Tooltip placement="bottomRight" title="Create new device">
          <Button icon="plus-square-o" size="small" onClick={this.handleNewDeviceClick.bind(this)}/>
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
      </div>
    );
  }

}

export default DevicesToolbar;
