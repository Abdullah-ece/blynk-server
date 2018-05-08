import React from 'react';
import PropTypes from 'prop-types';
import {OTA_STATUS_VALUE, OTA_STATUSES } from 'services/Products';
import {getCalendarFormatDate} from 'services/Date';
import {Popover} from 'antd';
import './styles.less';

class OTAStatus extends React.Component {

  static propTypes = {
    status: PropTypes.string,
    disconnectTime: PropTypes.number,

    deviceOtaInfo: PropTypes.shape({
      otaInitiatedAt: PropTypes.number,
      requestSentAt: PropTypes.number,
    })
  };

  getStatusLabel(status, text) {
    const statusLabels = {
      [OTA_STATUSES.STARTED]           : `Waiting for device to become online. Started: ${this.props.deviceOtaInfo && getCalendarFormatDate(this.props.deviceOtaInfo.otaInitiatedAt)}`,
      [OTA_STATUSES.REQUEST_SENT]      : `Waiting for device to download firmware. Request sent: ${this.props.deviceOtaInfo && getCalendarFormatDate(this.props.deviceOtaInfo.requestSentAt)}`,
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

    /*
      otaInitiatedAt
      requestSentAt
    */

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
