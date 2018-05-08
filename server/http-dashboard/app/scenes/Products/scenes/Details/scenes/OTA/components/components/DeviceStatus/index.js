import React from 'react';
import PropTypes from 'prop-types';
import {Popover} from 'antd';
import {convertTimeStampToTime} from 'services/Time';

import './styles.less';

class DeviceStatus extends React.Component {

  static propTypes = {
    status: PropTypes.string,
    disconnectTime: PropTypes.number,
  };

  render(){

    return(
      <div>
        {this.props.status === "ONLINE" && (<div className={"devices-list-item-status-online"} />) ||
          (
            <Popover content={
              this.props.disconnectTime === 0 && (<div>wasn't online yet</div>) || (<div>{"Offline for "+ convertTimeStampToTime(Date.now() - this.props.disconnectTime)}</div>)} >
              <div className={"devices-list-item-status-offline"} />
            </Popover>
          )}
      </div>
    );
  }
}

export default DeviceStatus;
