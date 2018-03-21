import React from 'react';
import {Timeline as Timelines, Icon} from 'antd';
import Event from './../Event';
import TypeFiltering from './../TypeFiltering';
import TimeFiltering from './../TimeFiltering';
import {reduxForm} from 'redux-form';
import {TIMELINE_ITEMS_PER_PAGE} from 'services/Devices';
import './styles.less';

@reduxForm()
class Timeline extends React.Component {

  static propTypes = {
    timeline: React.PropTypes.object,
    loading: React.PropTypes.bool,
    formValues: React.PropTypes.object,
    params: React.PropTypes.object,
    onMarkAsResolved: React.PropTypes.func,
    page: React.PropTypes.number,
    loadNextPage: React.PropTypes.func
  };

  getLimitForCurrentPage() {
    return this.props.page * TIMELINE_ITEMS_PER_PAGE;
  }

  getEventsForCurrentPage() {

    return this.props.timeline.logEvents.slice(0, this.getLimitForCurrentPage());
  }

  render() {

    let pending = null;
    if(this.props.timeline.logEvents &&  this.props.timeline.logEvents.length > this.props.page * TIMELINE_ITEMS_PER_PAGE) {
      pending = <a href="javascript:void(0)" onClick={this.props.loadNextPage.bind(this)}>Load more</a>;
    }

    if(!this.props.timeline)
      return null;

    return (
      <div className="devices--device-timeline-timeline">
        <TimeFiltering name="time"
                       formValues={this.props.formValues}/>
        <TypeFiltering name="type"
                       totalCritical={(this.props.timeline.totalCritical && this.props.timeline.totalCritical) || 0}
                       totalWarning={(this.props.timeline.totalWarning && this.props.timeline.totalWarning) || 0}
                       totalResolved={(this.props.timeline.totalResolved && this.props.timeline.totalResolved) || 0}/>
        { this.props.loading && (
          <Icon type="loading" className="devices--device-timeline-events devices--device-timeline-events--loading"/>
        ) || this.props.timeline.logEvents && (
          <Timelines className="devices--device-timeline-events"
                     pending={pending}>
            { this.getEventsForCurrentPage().map((event, key) => (
              <Event params={this.props.params} event={event} key={key}
                     onMarkAsResolved={this.props.onMarkAsResolved.bind(this)}/>
            ))}
          </Timelines>
        )}

        { (!this.props.loading && this.props.timeline.logEvents && !this.props.timeline.logEvents.length) || (!this.props.timeline.logEvents) && (
          <div className="no-timeline">No such events during this period</div>
        )}

      </div>
    );
  }

}

export default Timeline;
