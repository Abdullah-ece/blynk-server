import React          from 'react';
import classnames     from 'classnames';
import {Timeline}     from 'antd';
import {EVENT_TYPES}  from 'services/Products';
import './styles.less';

class Event extends React.Component {

  static propTypes = {
    event: React.PropTypes.object
  };

  getPropsByType(type) {

    if (type === EVENT_TYPES.ONLINE) {
      return {
        color: 'gray'
      };
    }

    if (type === EVENT_TYPES.OFFLINE) {
      return {
        color: 'gray'
      };
    }

    if (type === EVENT_TYPES.INFO) {
      return {
        color: 'blue'
      };
    }

    if (type === EVENT_TYPES.WARNING) {
      return {
        color: 'orange'
      };
    }

    if (type === EVENT_TYPES.CRITICAL) {
      return {
        color: 'red'
      };
    }

    return {};
  }

  render() {

    const className = classnames({
      "devices--device-timeline--event": true,
      "devices--device-timeline--event-default": [EVENT_TYPES.OFFLINE, EVENT_TYPES.ONLINE].indexOf(this.props.event.get('eventType')) >= 0,
      "devices--device-timeline--event-critical": this.props.event.get('eventType') === EVENT_TYPES.CRITICAL,
      "devices--device-timeline--event-warning": this.props.event.get('eventType') === EVENT_TYPES.WARNING,
      "devices--device-timeline--event-info": this.props.event.get('eventType') === EVENT_TYPES.INFO,
    });

    return (
      <Timeline.Item className={className}>
        <div className="devices--device-timeline--event-header">
          <div className="devices--device-timeline--event-header-name">
            { this.props.event.get('name') }
          </div>
          <div className="devices--device-timeline--event-header-time">
            Yesterday, 03:20 PM
          </div>
        </div>
        <div className="devices--device-timeline--event-content">
          A flush error happens when no contact is detected (switch or jumper) at the flow switch terminals while only
          the Flush Pump runs. Flush errors are indicated by the Remote Control flashing and F in the pump window.
        </div>
      </Timeline.Item>
    );
  }

}

export default Event;
