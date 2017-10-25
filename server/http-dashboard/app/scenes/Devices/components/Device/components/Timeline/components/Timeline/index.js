import React from 'react';
import {Timeline as Timelines, Icon} from 'antd';
import {Map} from 'immutable';
import Event from './../Event';
import TypeFiltering from './../TypeFiltering';
import TimeFiltering from './../TimeFiltering';
import {reduxForm} from 'redux-form';
import {TIMELINE_ITEMS_PER_PAGE} from 'services/Devices';
import './styles.less';

@reduxForm()
class Timeline extends React.Component {

  static propTypes = {
    timeline: React.PropTypes.instanceOf(Map),
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
    return this.props.timeline.get('logEvents').splice(
      this.getLimitForCurrentPage()
    );
  }

  render() {

    let pending = null;
    if(this.props.timeline.has('logEvents') &&  this.props.timeline.get('logEvents').size > this.props.page * TIMELINE_ITEMS_PER_PAGE) {
      pending = <a href="javascript:void(0)" onClick={this.props.loadNextPage.bind(this)}>Load more</a>;
    }

    return (
      <div className="devices--device-timeline-timeline">
        <TimeFiltering name="time"
                       formValues={this.props.formValues}/>
        <TypeFiltering name="type"
                       totalCritical={(this.props.timeline.has('totalCritical') && this.props.timeline.get('totalCritical')) || 0}
                       totalWarning={(this.props.timeline.has('totalWarning') && this.props.timeline.get('totalWarning')) || 0}
                       totalResolved={(this.props.timeline.has('totalResolved') && this.props.timeline.get('totalResolved')) || 0}/>
        { this.props.loading && (
          <Icon type="loading" className="devices--device-timeline-events devices--device-timeline-events--loading"/>
        ) || this.props.timeline.has('logEvents') && (
          <Timelines className="devices--device-timeline-events"
                     pending={pending}>
            { this.getEventsForCurrentPage().map((event, key) => (
              <Event params={this.props.params} event={event} key={key}
                     onMarkAsResolved={this.props.onMarkAsResolved.bind(this)}/>
            ))}
          </Timelines>
        )}

        { (!this.props.loading && this.props.timeline.has('logEvents') && !this.props.timeline.get('logEvents').size) || (!this.props.timeline.has('logEvents')) && (
          <div className="no-timeline">No such events during this period</div>
        )}

      </div>
    );
  }

}

export default Timeline;
