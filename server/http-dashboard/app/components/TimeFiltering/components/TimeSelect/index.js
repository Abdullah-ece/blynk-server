import React from 'react';
import {Radio} from 'antd';
import PropTypes from 'prop-types';

class TimeSelect extends React.Component {

  static propTypes = {
    input: PropTypes.object,
    options: PropTypes.array,
  };

  state = {
    isModalVisible: false
  };

  render() {
    return (
      <div className="devices--device-dashboard-time-filtering-time-select">
        <Radio.Group {...this.props.input}>

          {this.props.options.map((option) => (
            <Radio.Button value={option.key} key={option.key}>
              {option.value}
            </Radio.Button>
          ))}
        </Radio.Group>
      </div>
    );
  }

}

export default TimeSelect;
