import React from 'react';
import PropTypes from 'prop-types';
import {OTA_STATUS_VALUE} from 'services/Products';
import './styles.less';

class OTAStatus extends React.Component {

  static propTypes = {
    status: PropTypes.string,
    disconnectTime: PropTypes.number,
  };

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
          {statusText || "Never Updated"}
        </div>

      </div>
    );
  }
}

export default OTAStatus;
