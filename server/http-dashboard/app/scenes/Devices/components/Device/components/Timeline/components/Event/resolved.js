import React                              from 'react';
import classnames                         from 'classnames';
import {Timeline}                         from 'antd';
import {EVENT_TYPES, getEventDefaultName} from 'services/Products';
import {getCalendarFormatDate} from 'services/Date';

class Resolved extends React.Component {

  static propTypes = {
    event: React.PropTypes.object
  };

  render() {

    const resolvedTime = getCalendarFormatDate(this.props.event.get('resolvedAt'));

    const time = getCalendarFormatDate(this.props.event.get('ts'));

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
              { this.props.event.get('name') || getEventDefaultName(this.props.event.get('eventType')) }
            </div>
            <div className="devices--device-timeline--event-resolved-content-time">
              {time}
            </div>
          </div>
          <div className="devices--device-timeline--event-resolved-description">
            {this.props.event.has('resolvedComment') && (
              this.props.event.get('resolvedComment').split('\n').map((item, key) => {
                return (<span key={key}>{item}<br/></span>);
              })
            ) || 'No comments provided'}
          </div>
        </div>
      </Timeline.Item>
    );
  }

}

export default Resolved;
