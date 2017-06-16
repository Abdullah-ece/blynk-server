import React from 'react';
import {Radio} from 'antd';
import {TIMELINE_TIME_FILTERS} from 'services/Devices';
import SpecificTimeSelect from "../SpecificTimeSelect/index";
import {change} from 'redux-form';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

@connect(() => ({}), (dispatch) => ({
  change: bindActionCreators(change, dispatch)
}))
class TimeSelect extends React.Component {

  static propTypes = {
    input: React.PropTypes.object,
    change: React.PropTypes.func,
  };

  state = {
    isModalVisible: false
  };

  handleSpecificTimeSelectChange(timestamp) {
    this.props.change('Timeline', 'time', timestamp.length ? 'NONE' : TIMELINE_TIME_FILTERS.HOUR.key);
    this.props.change('Timeline', 'customFrom', timestamp.length ? timestamp[0] : 0);
    this.props.change('Timeline', 'customTo', timestamp.length ? timestamp[1] : 0);
  }

  render() {
    return (
      <div className="devices--device-timeline--time-filtering">
        <div className="devices--device-timeline--time-filtering-time-select">
          <Radio.Group {...this.props.input}>
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
          </Radio.Group>
        </div>
        <div className="devices--device-timeline--time-filtering-specific-time-select">
          <SpecificTimeSelect onChange={this.handleSpecificTimeSelectChange.bind(this)}/>
        </div>
      </div>
    );
  }

}

export default TimeSelect;
