import React from 'react';
import PropTypes from 'prop-types';
import {OTA_STATUS_VALUE, OTA_STATUSES} from 'services/Products';
import {getCalendarFormatDate} from 'services/Date';
import {Popover} from 'antd';
import './styles.less';

class OTAStatus extends React.Component {

  static propTypes = {
    status        : PropTypes.string,
    disconnectTime: PropTypes.number,

    deviceShipmentInfo: PropTypes.shape({
      otaInitiatedAt     : PropTypes.number,
      requestSentAt      : PropTypes.number,
      firmwareUploadedAt : PropTypes.number,
      firmwareRequestedAt: PropTypes.number,
    })
  };

  getStatusLabel(status, text) {
    const statusLabels = {
      [OTA_STATUSES.STARTED]           : (
        <p>
          Waiting for device to become online. <br/> Firmware update started
          at: {this.props.deviceShipmentInfo && getCalendarFormatDate(this.props.deviceShipmentInfo.otaInitiatedAt)}
        </p>
      ),
      [OTA_STATUSES.REQUEST_SENT]      : (
        <p>
          Waiting for device to download firmware. <br/>Request sent at:
          {this.props.deviceShipmentInfo && getCalendarFormatDate(this.props.deviceShipmentInfo.requestSentAt)}
        </p>
      ),
      [OTA_STATUSES.FIRMWARE_REQUESTED]: (
        <p>
          Device started to download firmware. Waiting for device finish
          download. <br/>Download started at:
          {this.props.deviceShipmentInfo && getCalendarFormatDate(this.props.deviceShipmentInfo.firmwareRequestedAt)}
        </p>
      ),
      [OTA_STATUSES.FIRMWARE_UPLOADED] : (
        <p>
          Firmware already downloaded.  Waiting for device to update. <br/>Firmware
          downloaded at:
          {this.props.deviceShipmentInfo && getCalendarFormatDate(this.props.deviceShipmentInfo.firmwareUploadedAt)}
        </p>
      ),
    };

    if (statusLabels[status]) {
      return (<Popover content={statusLabels[status]}>
        {text}
      </Popover>);
    }

    return text;
  }

  render() {
    const statuses = {
      "Started"            : "warning",
      "Request sent"       : "warning",
      "Firmware requested" : "warning",
      "Firmware downloaded": "warning",
      "Updated"            : "positive",
      "Failure"            : "critical"
    };

    const statusText = OTA_STATUS_VALUE[this.props.status];
    const statusStyle = statuses[statusText] === undefined ? "disabled" : statuses[statusText];

    /*
      otaInitiatedAt
      requestSentAt
    */

    return (
      <div>
        <div>
          <div className={"devices-list-item-status-" + statusStyle}/>
          {this.getStatusLabel(this.props.status, statusText || "Never Updated")}
        </div>

      </div>
    );
  }
}

export default OTAStatus;
