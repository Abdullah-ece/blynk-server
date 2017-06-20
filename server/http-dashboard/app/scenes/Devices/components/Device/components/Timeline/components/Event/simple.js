import React          from 'react';
import classnames     from 'classnames';
import moment         from 'moment';
import {Timeline}     from 'antd';
import {EVENT_TYPES}  from 'services/Products';

class Simple extends React.Component {

  static propTypes = {
    event: React.PropTypes.object
  };

  render() {

    const time = moment(this.props.event.get('ts')).calendar(null, {
      sameDay: '[Today], hh:mm A',
      lastDay: '[Yesterday], hh:mm A',
      lastWeek: 'dddd, hh:mm A',
      sameElse: 'MMM D, YYYY hh:mm A'
    });

    const className = classnames({
      "devices--device-timeline--event": true,
      "devices--device-timeline--event-default": [EVENT_TYPES.OFFLINE, EVENT_TYPES.ONLINE].indexOf(this.props.event.get('eventType')) >= 0,
      "devices--device-timeline--event-critical": this.props.event.get('eventType') === EVENT_TYPES.CRITICAL,
      "devices--device-timeline--event-warning": this.props.event.get('eventType') === EVENT_TYPES.WARNING,
      "devices--device-timeline--event-info": this.props.event.get('eventType') === EVENT_TYPES.INFO,
      "devices--device-timeline--event-success": this.props.event.get('isResolved') === true,
    });

    return (
      <Timeline.Item className={className}>
        <div className="devices--device-timeline--event-header">
          <div className="devices--device-timeline--event-header-name">
            { this.props.event.get('name') }
          </div>
          <div className="devices--device-timeline--event-header-time">
            {time}
          </div>
        </div>
        <div className="devices--device-timeline--event-content">
          { this.props.event.get('description') }
        </div>
      </Timeline.Item>
    );
  }

}

export default Simple;
