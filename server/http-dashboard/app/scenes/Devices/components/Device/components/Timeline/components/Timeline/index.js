import React from 'react';
import {Timeline as Timelines, Icon} from 'antd';
import {Map} from 'immutable';
import Event from './../Event';
import TypeFiltering from './../TypeFiltering';
import TimeFiltering from './../TimeFiltering';
import {reduxForm} from 'redux-form';

@reduxForm()
class Timeline extends React.Component {

  static propTypes = {
    timeline: React.PropTypes.instanceOf(Map),
    loading: React.PropTypes.bool,
  };

  render() {

    return (
      <div className="devices--device-timeline-timeline">
        <TimeFiltering name="time"/>
        <TypeFiltering name="type"
                       totalCritical={(this.props.timeline.has('totalCritical') && this.props.timeline.get('totalCritical')) || 0}
                       totalWarning={(this.props.timeline.has('totalWarning') && this.props.timeline.get('totalWarning')) || 0}
                       totalResolved={(this.props.timeline.has('totalResolved') && this.props.timeline.get('totalResolved')) || 0}/>
        { this.props.loading && (
          <Icon type="loading" className="devices--device-timeline-events"/>
        ) || this.props.timeline.has('logEvents') && (
          <Timelines className="devices--device-timeline-events">
            { this.props.timeline.get('logEvents').map((event, key) => (
              <Event event={event} key={key}/>
            ))}
          </Timelines>
        )}

        { !this.props.loading && this.props.timeline.has('logEvents') && !this.props.timeline.get('logEvents').size && (
          <i>No one event</i>
        )}

      </div>
    );
  }

}

export default Timeline;
