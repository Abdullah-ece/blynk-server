import React from 'react';
import {fromJS} from 'immutable';
import {Timeline as Timelines} from './components';
import {TIMELINE_TYPE_FILTERS} from 'services/Devices';
import './styles.less';

class Timeline extends React.Component {

  handleValuesChange() {
    // console.log(props, dispatch);
  }

  render() {

    const timeline = fromJS({
      "totalWarning": 1,
      "logEvents": [
        {
          "id": 5,
          "deviceId": 1,
          "eventType": "CRITICAL",
          "ts": 1497433678865,
          "eventHashcode": 0,
          "isResolved": false
        },
        {
          "id": 4,
          "deviceId": 1,
          "eventType": "WARNING",
          "ts": 1497433678864,
          "eventHashcode": 0,
          "isResolved": false
        },
        {
          "id": 3,
          "deviceId": 1,
          "eventType": "INFORMATION",
          "ts": 1497433678863,
          "eventHashcode": 0,
          "isResolved": false
        },
        {
          "id": 2,
          "deviceId": 1,
          "eventType": "OFFLINE",
          "ts": 1497433678856,
          "eventHashcode": 0,
          "isResolved": false
        },
        {
          "id": 1,
          "deviceId": 1,
          "eventType": "ONLINE",
          "ts": 1497433678835,
          "eventHashcode": 0,
          "isResolved": false
        }
      ],
      "totalResolved": 0,
      "totalCritical": 1
    });

    const initialValues = {
      type: TIMELINE_TYPE_FILTERS.ALL.key,
      time: 'DAY',
      customFrom: 0,
      customTo: 0
    };

    return (
      <div className="devices--device-timeline">
        <Timelines timeline={timeline} initialValues={initialValues} form="Timeline"
                   onChange={this.handleValuesChange.bind(this)}/>
      </div>
    );
  }

}

export default Timeline;
