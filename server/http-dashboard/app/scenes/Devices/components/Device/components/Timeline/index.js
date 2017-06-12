import React from 'react';
import {Timeline as TimeLines} from 'antd';
import './styles.less';

class Timeline extends React.Component {

  render() {
    return (
      <div className="devices--device-timeline">
        <TimeLines className="devices--device-timeline-events">
          <TimeLines.Item>
            Flush error Yesterday, 03:20 PM<br/>
            A flush error happens when no contact is detected (switch or jumper) at the flow switch terminals while only
            the Flush Pump runs. Flush errors are indicated by the Remote Control flashing and F in the pump window.
          </TimeLines.Item>
          <TimeLines.Item>
            Flush error Yesterday, 03:20 PM<br/>
            A flush error happens when no contact is detected (switch or jumper) at the flow switch terminals while only
            the Flush Pump runs. Flush errors are indicated by the Remote Control flashing and F in the pump window.
          </TimeLines.Item>
          <TimeLines.Item>
            Flush error Yesterday, 03:20 PM<br/>
            A flush error happens when no contact is detected (switch or jumper) at the flow switch terminals while only
            the Flush Pump runs. Flush errors are indicated by the Remote Control flashing and F in the pump window.
          </TimeLines.Item>
          <TimeLines.Item>
            Flush error Yesterday, 03:20 PM<br/>
            A flush error happens when no contact is detected (switch or jumper) at the flow switch terminals while only
            the Flush Pump runs. Flush errors are indicated by the Remote Control flashing and F in the pump window.
          </TimeLines.Item>
          <TimeLines.Item>
            Flush error Yesterday, 03:20 PM<br/>
            A flush error happens when no contact is detected (switch or jumper) at the flow switch terminals while only
            the Flush Pump runs. Flush errors are indicated by the Remote Control flashing and F in the pump window.
          </TimeLines.Item>
        </TimeLines>
      </div>
    );
  }

}

export default Timeline;
