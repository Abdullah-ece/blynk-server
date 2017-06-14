import React from 'react';
import {Timeline as Timelines} from 'antd';
import {Map} from 'immutable';
import Event from './../Event';
import TypeFiltering from './../TypeFiltering';
import TimeFiltering from './../TimeFiltering';
import {reduxForm} from 'redux-form';

@reduxForm()
class Timeline extends React.Component {

  static propTypes = {
    timeline: React.PropTypes.instanceOf(Map),
  };

  render() {

    return (
      <div className="devices--device-timeline">
        <TimeFiltering name="time"/>
        <TypeFiltering name="type"
                       totalCritical={this.props.timeline.get('totalCritical')}
                       totalWarning={this.props.timeline.get('totalWarning')}
                       totalResolved={this.props.timeline.get('totalResolved')}/>
        <Timelines className="devices--device-timeline-events">
          { this.props.timeline.get('logEvents').map((event, key) => (
            <Event event={event} key={key}/>
          ))}
        </Timelines>
      </div>
    );
  }

}

export default Timeline;
