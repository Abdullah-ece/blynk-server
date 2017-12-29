import React                              from 'react';
import classnames                         from 'classnames';
import {Timeline, Button}                 from 'antd';
import {EVENT_TYPES, getEventDefaultName} from 'services/Products';
import {getCalendarFormatDate} from 'services/Date';

class Simple extends React.Component {

  static propTypes = {
    event: React.PropTypes.object,
    onMarkAsResolved: React.PropTypes.func,
  };

  handleOk() {
    this.props.onMarkAsResolved(this.props.event.get('id'));
  }

  render() {
    const time = getCalendarFormatDate(this.props.event.get('ts'));

    const className = classnames({
      "devices--device-timeline--event": true,
      "devices--device-timeline--event-default": [EVENT_TYPES.OFFLINE, EVENT_TYPES.ONLINE].indexOf(this.props.event.get('eventType')) >= 0,
      "devices--device-timeline--event-critical": this.props.event.get('eventType') === EVENT_TYPES.CRITICAL,
      "devices--device-timeline--event-warning": this.props.event.get('eventType') === EVENT_TYPES.WARNING,
      "devices--device-timeline--event-info": this.props.event.get('eventType') === EVENT_TYPES.INFO,
      "devices--device-timeline--event-success": this.props.event.get('isResolved') === true,
    });

    const canBeResolved = this.props.event.get('eventType') === EVENT_TYPES.CRITICAL || this.props.event.get('eventType') === EVENT_TYPES.WARNING;

    return (
      <Timeline.Item className={className}>
        <div className="devices--device-timeline--event-header">
          <div className="devices--device-timeline--event-header-name">
            { this.props.event.get('name') || getEventDefaultName(this.props.event.get('eventType')) }
          </div>
          <div className="devices--device-timeline--event-header-time">
            {time}
          </div>
        </div>
        { this.props.event.has('description') && (
          <div className="devices--device-timeline--event-content">
            { this.props.event.get('description').split('\n').map((item, key) => {
              return (<span key={key}>{item}<br/></span>);
            })}
          </div>
        )}

        {canBeResolved && (
          <Button icon="check-circle-o"
                  type="primary"
                  className="devices--device-timeline--event-mark-as-resolved-lg positive"
                  onClick={this.handleOk.bind(this)}>
            Mark as resolved
          </Button>
        )}

        {canBeResolved && (
          <Button icon="check-circle-o"
                  type="primary"
                  className="devices--device-timeline--event-mark-as-resolved-sm positive"
                  onClick={this.handleOk.bind(this)}/>
        )}
      </Timeline.Item>
    );
  }

}

export default Simple;
