import React from 'react';
import {Radio} from 'antd';
import {TIMELINE_TIME_FILTERS} from 'services/Devices';
class TimeSelect extends React.Component {

  static propTypes = {
    input: React.PropTypes.object,
  };

  state = {
    isModalVisible: false
  };

  render() {
    return (
      <div className="devices--device-timeline--time-filtering-time-select">
        <Radio.Group {...this.props.input}>
          <Radio.Button value={TIMELINE_TIME_FILTERS.HOUR.key}>
            {TIMELINE_TIME_FILTERS.HOUR.value}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS['6HOURS'].key}>
            {TIMELINE_TIME_FILTERS['6HOURS'].value}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS.DAY.key}>
            {TIMELINE_TIME_FILTERS.DAY.value}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS.WEEK.key}>
            {TIMELINE_TIME_FILTERS.WEEK.value}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS.MONTH.key}>
            {TIMELINE_TIME_FILTERS.MONTH.value}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS['3MONTHS'].key}>
            {TIMELINE_TIME_FILTERS['3MONTHS'].value}
          </Radio.Button>
          <Radio.Button value={TIMELINE_TIME_FILTERS.CUSTOM.key}>
            {TIMELINE_TIME_FILTERS.CUSTOM.value}
          </Radio.Button>
        </Radio.Group>
      </div>
    );
  }

}

export default TimeSelect;
