import React from 'react';
import {DatePicker} from 'antd';

class SpecificTimeSelect extends React.Component {

  static propTypes = {
    visible: React.PropTypes.bool,
    onChange: React.PropTypes.func,
    onCancel: React.PropTypes.func,
  };

  handleCancelClick() {
    this.props.onCancel();
  }

  handleChange(time) {
    if (!time.length) {
      this.props.onChange([]);
    }
  }

  handleOkClick(value) {
    this.props.onChange([
      value[0].unix() * 1000,
      value[1].unix() * 1000,
    ]);
  }

  render() {
    return (
      <DatePicker.RangePicker onChange={this.handleChange.bind(this)} format="MMM D, YYYY hh:mm A" showTime={true}
                              style={{width: '100%'}} onOk={this.handleOkClick.bind(this)}/>
    );
  }

}

export default SpecificTimeSelect;
