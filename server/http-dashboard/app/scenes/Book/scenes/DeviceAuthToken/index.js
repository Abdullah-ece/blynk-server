import React from 'react';
import Highlight from 'react-highlight';
import {DeviceAuthToken} from 'components';

class DeviceAuthTokenBook extends React.Component {

  render() {
    return (
      <div>

        <h4>Example</h4>

        <DeviceAuthToken authToken="ryfod74n56f9dn53f0dh4n56fksntbftsu3k4" onCopy={() => {/* do something */
        }}/>

        <h4>Code</h4>

        <Highlight>
          {`<DeviceAuthToken authToken="ryfod74n56f9dn53f0dh4n56fksntbftsu3k4" onCopy={() => { /* do something */ }}/>`}
        </Highlight>

      </div>
    );
  }

}

export default DeviceAuthTokenBook;
