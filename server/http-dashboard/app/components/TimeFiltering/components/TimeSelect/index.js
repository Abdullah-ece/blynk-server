import React from 'react';
import {Radio} from 'antd';
import {TIMELINE_TIME_FILTERS} from 'services/Devices';

const labels = {
  'LIVE' : 'Live',
  'HOUR': 'Hour',
  'DAY': 'Day',
  'WEEK': 'Week',
  'MONTH': 'Month',
  'CUSTOM': 'Custom Range'
};

class TimeSelect extends React.Component {

  static propTypes = {
    input: React.PropTypes.object,
  };

  state = {
    isModalVisible: false
  };

  render() {
    return (
      <div className="devices--device-dashboard-time-filtering-time-select">
        <Radio.Group {...this.props.input}>
          <Radio.Button value={TIMELINE_TIME_FILTERS.LIVE.key}>
            {labels[TIMELINE_TIME_FILTERS.LIVE.key]}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS.HOUR.key}>
            {labels[TIMELINE_TIME_FILTERS.HOUR.key]}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS.DAY.key}>
            {labels[TIMELINE_TIME_FILTERS.DAY.key]}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS.WEEK.key}>
            {labels[TIMELINE_TIME_FILTERS.WEEK.key]}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS.MONTH.key}>
            {labels[TIMELINE_TIME_FILTERS.MONTH.key]}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS.CUSTOM.key}>
            {labels[TIMELINE_TIME_FILTERS.CUSTOM.key]}
          </Radio.Button>
        </Radio.Group>
      </div>
    );
  }

}

export default TimeSelect;
