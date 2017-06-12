import React from 'react';
import {Timeline as TimeLines} from 'antd';
import {fromJS} from 'immutable';
import {Event} from './components';
import './styles.less';

class Timeline extends React.Component {

  render() {

    const timeline = fromJS([
      {
        "id": 1,
        "deviceId": 1,
        "eventType": "ONLINE",
        "ts": 1496949455443,
        "eventHashcode": 613812780,
        "description": "MyNewDescription",
        "isResolved": true,
        "resolvedBy": "Vasya Pupkin",
        "name": "Temp is super high"
      },
      {
        "id": 1,
        "deviceId": 1,
        "eventType": "OFFLINE",
        "ts": 1496949455443,
        "eventHashcode": 613812780,
        "description": "MyNewDescription",
        "isResolved": true,
        "resolvedBy": "Vasya Pupkin",
        "name": "Temp is super high"
      },
      {
        "id": 1,
        "deviceId": 1,
        "eventType": "CRITICAL",
        "ts": 1496949455443,
        "eventHashcode": 613812780,
        "description": "MyNewDescription",
        "isResolved": true,
        "resolvedBy": "Vasya Pupkin",
        "name": "Temp is super high"
      },
      {
        "id": 1,
        "deviceId": 1,
        "eventType": "WARNING",
        "ts": 1496949455443,
        "eventHashcode": 613812780,
        "description": "MyNewDescription",
        "isResolved": true,
        "resolvedBy": "Vasya Pupkin",
        "name": "Temp is super high"
      },
      {
        "id": 1,
        "deviceId": 1,
        "eventType": "INFORMATION",
        "ts": 1496949455443,
        "eventHashcode": 613812780,
        "description": "MyNewDescription",
        "isResolved": true,
        "resolvedBy": "Vasya Pupkin",
        "name": "Temp is super high"
      }
    ]);

    return (
      <div className="devices--device-timeline">
        <TimeLines className="devices--device-timeline-events">
          { timeline.map((event, key) => (
            <Event event={event} key={key}/>
          ))}
        </TimeLines>
      </div>
    );
  }

}

export default Timeline;
