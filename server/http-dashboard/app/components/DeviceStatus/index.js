import React from 'react';
import classnames from 'classnames';
import './styles.less';

class DeviceStatus extends React.Component {

  static propTypes = {
    status: React.PropTypes.string
  };

  ONLINE = 'online';
  OFFLINE = 'offline';

  getStatusByProps(status) {
    if (status === this.ONLINE) {
      return 'Online';
    } else if (status === this.OFFLINE) {
      return 'Offline';
    } else {
      throw new Error('Missing "status" parameter for DeviceStatus');
    }
  }

  render() {

    const className = classnames({
      'device-status': true,
      'device-status-online': this.props.status === this.ONLINE,
      'device-status-offline': this.props.status === this.OFFLINE,
    });

    return (
      <div className={className}>
        { this.getStatusByProps(this.props.status) }
      </div>
    );
  }

}

export default DeviceStatus;
