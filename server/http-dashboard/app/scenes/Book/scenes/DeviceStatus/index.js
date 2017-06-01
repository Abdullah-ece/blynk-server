import React from 'react';
import Highlight from 'react-highlight';
import {DeviceStatus} from 'components';

class DeviceStatusBook extends React.Component {

  render() {
    return (
      <div>

        <h4>Example</h4>

        <DeviceStatus status="online"/>

        <h4>Code</h4>

        <Highlight>
          {`<DeviceStatus status="online" />`}
        </Highlight>

        <h4>Example</h4>

        <DeviceStatus status="offline"/>

        <h4>Code</h4>

        <Highlight>
          {`<DeviceStatus status="offline" />`}
        </Highlight>

      </div>
    );
  }

}

export default DeviceStatusBook;
