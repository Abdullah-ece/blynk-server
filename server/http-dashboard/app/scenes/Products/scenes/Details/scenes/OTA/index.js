import React from 'react';
import OTA from './components';
import PropTypes from 'prop-types';

class OTAScene extends React.Component {

  static propTypes = {
    devices: PropTypes.arrayOf(PropTypes.shape({
      id            : PropTypes.number,
      name          : PropTypes.string,
      status        : PropTypes.oneOf(['ONLINE', 'OFFLINE']), // use this for column "status" and display like a green / gray dot
      disconnectTime: PropTypes.number, // display "Was online N days ago" when user do mouseover the gray dot (idea is to display last time when device was online if it's offline right now)
      hardwareInfo  : PropTypes.shape({
        version: PropTypes.string
      })
    }))
  };

  render() {

    const devices = [{
      id            : 1,
      name          : 'Device 1',
      status        : 'ONLINE',
      disconnectTime: 1525684476846,
      hardwareInfo  : {
        version: '0.0.1'
      }
    }, {
      id            : 2,
      name          : 'Device 2',
      status        : 'OFFLINE',
      disconnectTime: 1525684476846,
      hardwareInfo  : {
        version: '0.0.2'
      }
    }];

    return (
      <OTA devices={devices}/>
    );
  }
}

export default OTAScene;
