import React from 'react';
import {Map} from 'immutable';
import {Timeline as Timelines, MarkAsResolvedModal} from './components';
import {TIMELINE_TYPE_FILTERS, TIMELINE_TIME_FILTERS} from 'services/Devices';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {getFormValues} from 'redux-form';
import {TimelineFetch} from 'data/Devices/api';
import './styles.less';

@connect((state) => ({
  timeline: state.Devices.get('timeline'),
  formValues: getFormValues('Timeline')(state)
}), (dispatch) => ({
  fetchTimeline: bindActionCreators(TimelineFetch, dispatch)
}))
class Timeline extends React.Component {

  static propTypes = {
    timeline: React.PropTypes.instanceOf(Map),
    fetchTimeline: React.PropTypes.func,
    formValues: React.PropTypes.object,
    params: React.PropTypes.object,
  };

  state = {
    loading: false,
    isResolveModalVisible: false
  };

  componentDidMount() {

    const params = {};

    params.from = new Date().getTime() - TIMELINE_TIME_FILTERS.HOUR.time;

    this.fetchTimeline(params);
  }

  fetchTimeline(params = {}) {
    this.setState({
      loading: true
    });
    this.props.fetchTimeline({
      ...params,
      deviceId: this.props.params.id
    }).then(() => {
      this.setState({
        loading: false
      });
    });
  }

  handleValuesChange(values) {

    const params = {};

    if ([TIMELINE_TYPE_FILTERS.CRITICAL.key, TIMELINE_TYPE_FILTERS.WARNING.key].indexOf(values.type) >= 0) {
      params.eventType = values.type;
      params.isResolved = false;
    }

    if (TIMELINE_TYPE_FILTERS.RESOLVED.key === values.type) {
      params.isResolved = true;
    }

    if (values.time === TIMELINE_TIME_FILTERS.CUSTOM.key) {
      if (!values.customTime.length)
        return false;
      params.from = values.customTime[0];
      params.to = values.customTime[1];
    } else {
      params.from = new Date().getTime() - TIMELINE_TIME_FILTERS[values.time].time;
    }

    this.fetchTimeline(params);
  }

  handleCancel() {
    this.setState({
      isResolveModalVisible: false,
    });
  }

  handleMarkAsResolved(eventId) {
    this.setState({
      isResolveModalVisible: true,
      eventId: eventId
    });
  }

  handleMarkAsResolvesSuccess() {
    this.handleCancel();
    this.handleValuesChange(this.props.formValues);
  }

  render() {

    const initialValues = {
      type: TIMELINE_TYPE_FILTERS.ALL.key,
      time: TIMELINE_TIME_FILTERS.HOUR.key,
      customTime: []
    };

    return (
      <div className="devices--device-timeline">
        { this.props.timeline.has('logEvents') && (
          <MarkAsResolvedModal isModalVisible={this.state.isResolveModalVisible}
                               onCancel={this.handleCancel.bind(this)}
                               deviceId={Number(this.props.params.id)}
                               event={this.props.timeline.get('logEvents').find(event => event.get('id') === this.state.eventId)}
                               onSuccess={this.handleMarkAsResolvesSuccess.bind(this)}/>
        )}
        <Timelines form="Timeline"
                   timeline={this.props.timeline}
                   loading={this.state.loading}
                   initialValues={initialValues}
                   formValues={this.props.formValues}
                   params={this.props.params}
                   onChange={this.handleValuesChange.bind(this)}
                   onMarkAsResolved={this.handleMarkAsResolved.bind(this)}/>
      </div>
    );
  }

}

export default Timeline;
