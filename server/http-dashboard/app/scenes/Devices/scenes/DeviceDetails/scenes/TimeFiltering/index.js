import React from 'react';
import {TimeFiltering} from 'components';
import {
  DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME,
  TIMELINE_TIME_FILTERS
} from 'services/Devices';
import moment from 'moment';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {change} from 'redux-form';
import PropTypes from 'prop-types';

@connect(() => ({}), (dispatch) => ({
  change: bindActionCreators(change, dispatch)
}))
class TimeFilteringScene extends React.Component {

  static propTypes = {
    change: PropTypes.func,
  };

  constructor(props) {
    super(props);
  }

  componentWillUnmount() {
    this.props.change(DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME, {
      time: TIMELINE_TIME_FILTERS.LIVE.key,
      customTime: [
        moment().subtract(7, 'day').valueOf(),
        moment().valueOf()
      ]
    });
  }

  options = [
    {
      key: TIMELINE_TIME_FILTERS.LIVE.key,
      value: TIMELINE_TIME_FILTERS.LIVE.value,
    },
    {
      key: TIMELINE_TIME_FILTERS.ONE_HOUR.key,
      value: TIMELINE_TIME_FILTERS.ONE_HOUR.value,
    },
    {
      key: TIMELINE_TIME_FILTERS.SIX_HOURS.key,
      value: TIMELINE_TIME_FILTERS.SIX_HOURS.value,
    },
    {
      key: TIMELINE_TIME_FILTERS.DAY.key,
      value: TIMELINE_TIME_FILTERS.DAY.value,
    },
    {
      key: TIMELINE_TIME_FILTERS.WEEK.key,
      value: TIMELINE_TIME_FILTERS.WEEK.value,
    },
    {
      key: TIMELINE_TIME_FILTERS.MONTH.key,
      value: TIMELINE_TIME_FILTERS.MONTH.value,
    },
    {
      key: TIMELINE_TIME_FILTERS['3MONTHS'].key,
      value: TIMELINE_TIME_FILTERS['3MONTHS'].value,
    },
    {
      key: TIMELINE_TIME_FILTERS.CUSTOM.key,
      value: TIMELINE_TIME_FILTERS.CUSTOM.value,
      isCustomTime: true,
    }

  ];

  render() {

    const initialValues = {
      time: TIMELINE_TIME_FILTERS.LIVE.key,
      customTime: [
        moment().subtract(7, 'day').valueOf(),
        moment().valueOf()
      ]
    };

    return (
      <TimeFiltering form={DEVICE_DASHBOARD_TIME_FILTERING_FORM_NAME} initialValues={initialValues} options={this.options}/>
    );
  }

}

export default TimeFilteringScene;
