import React                     from 'react';
import classnames                from 'classnames';
import moment                    from 'moment';
import {Timeline}                from 'antd';
import {EVENT_TYPES}             from 'services/Products';

class Resolved extends React.Component {

  static propTypes = {
    event: React.PropTypes.object
  };

  render() {

    const resolvedTime = moment(this.props.event.get('resolvedTs') || new Date().getTime()).calendar(null, {
      sameDay: '[today], hh:mm A',
      lastDay: '[yesterday], hh:mm A',
      lastWeek: 'dddd, hh:mm A',
      sameElse: 'MMM D, YYYY hh:mm A'
    });

    const time = moment(this.props.event.get('ts')).calendar(null, {
      sameDay: '[Today], hh:mm A',
      lastDay: '[Yesterday], hh:mm A',
      lastWeek: 'dddd, hh:mm A',
      sameElse: 'MMM D, YYYY hh:mm A'
    });

    const className = classnames({
      "devices--device-timeline--event-resolved-content-name": true,
      "devices--device-timeline--event-resolved-content-name-critical": this.props.event.get('eventType') === EVENT_TYPES.CRITICAL,
      "devices--device-timeline--event-resolved-content-name-warning": this.props.event.get('eventType') === EVENT_TYPES.WARNING,
    });
    return (
      <Timeline.Item className="devices--device-timeline--event devices--device-timeline--event-success">
        <div className="devices--device-timeline--event-header">
          <div className="devices--device-timeline--event-header-name-resolved-icon">resolved</div>
          <div className="devices--device-timeline--event-header-resolved-by">
            { `by ${this.props.event.get('resolvedBy')}, ${resolvedTime}`}
          </div>
        </div>
        <div className="devices--device-timeline--event-content">
          <div className="devices--device-timeline--event-resolved-content">
            <div className={className}>
              { this.props.event.get('name') }
            </div>
            <div className="devices--device-timeline--event-resolved-content-time">
              {time}
            </div>
          </div>
          { this.props.event.has('description') && (
            <div className="devices--device-timeline--event-resolved-description">
              { this.props.event.get('resolvedComment') || 'No comments provided' }
            </div>
          )}
        </div>
      </Timeline.Item>
    );
  }

}

export default Resolved;
