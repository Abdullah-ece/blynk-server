import React from 'react';
import {Map} from 'immutable';
import {Timeline as Timelines} from './components';
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

  render() {

    const initialValues = {
      type: TIMELINE_TYPE_FILTERS.ALL.key,
      time: TIMELINE_TIME_FILTERS.HOUR.key,
      customTime: []
    };

    return (
      <div className="devices--device-timeline">
        <Timelines form="Timeline"
                   timeline={this.props.timeline}
                   loading={this.state.loading}
                   initialValues={initialValues}
                   formValues={this.props.formValues}
                   onChange={this.handleValuesChange.bind(this)}/>
      </div>
    );
  }

}

export default Timeline;
