import React from 'react';
import PropTypes from 'prop-types';
import {OTA_STATUS_VALUE, OTA_STATUSES } from 'services/Products';
import {Popover} from 'antd';
import './styles.less';

class OTAStatus extends React.Component {

  static propTypes = {
    status: PropTypes.string,
    disconnectTime: PropTypes.number,
  };

  getStatusLabel(status, text) {
    const statusLabels = {
      [OTA_STATUSES.STARTED]           : "Waiting for device to become online",
      [OTA_STATUSES.REQUEST_SENT]      : "Waiting for device to download firmware",
      [OTA_STATUSES.FIRMWARE_UPLOADED] : "Firmware already downloaded. Waiting for device to update",
    };

    if(statusLabels[status]){
      return (<Popover content={statusLabels[status]}>
        {text}
      </Popover>);
    }

    return text;
  }

  render(){
    const statuses = {
      "Pending": "warning",
      "Success": "positive",
      "Failure": "critical"
    };

    const statusText = OTA_STATUS_VALUE[this.props.status];
    const statusStyle= statuses[statusText] === undefined ? "disabled" : statuses[statusText] ;

    return(
      <div>
        <div>
          <div className={"devices-list-item-status-" + statusStyle} />
          {this.getStatusLabel(this.props.status, statusText || "Never Updated")}
        </div>

      </div>
    );
  }
}

export default OTAStatus;
