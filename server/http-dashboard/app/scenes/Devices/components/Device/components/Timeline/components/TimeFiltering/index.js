import React from 'react';
import {Radio} from 'antd';
import './styles.less';

class TimeFiltering extends React.Component {

  render() {
    return (
      <div className="devices--device-timeline--time-filtering">
        <Radio.Group>
          <Radio.Button value="hour">
            1 hour
          </Radio.Button>
          <Radio.Button value="day">
            1 day
          </Radio.Button>
          <Radio.Button value="week">
            1 week
          </Radio.Button>
          <Radio.Button value="month">
            Month
          </Radio.Button>
          <Radio.Button value="custom">
            Custom Range
          </Radio.Button>
        </Radio.Group>
      </div>
    );
  }

}

export default TimeFiltering;
