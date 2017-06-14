import React from 'react';
import {Radio} from 'antd';
import {Field} from 'redux-form';
import {TIMELINE_TIME_FILTERS} from 'services/Devices';
import './styles.less';

class TimeFiltering extends React.Component {

  component({input}) {
    return (
      <div className="devices--device-timeline--time-filtering">
        <Radio.Group {...input}>
          <Radio.Button value={TIMELINE_TIME_FILTERS.HOUR.key}>
            {TIMELINE_TIME_FILTERS.HOUR.value}
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
          <Radio.Button value={TIMELINE_TIME_FILTERS.CUSTOM.key}>
            {TIMELINE_TIME_FILTERS.CUSTOM.value}
          </Radio.Button>
        </Radio.Group>
      </div>
    );
  }

  render() {
    return (
      <Field {...this.props} component={this.component}/>
    );
  }

}

export default TimeFiltering;
