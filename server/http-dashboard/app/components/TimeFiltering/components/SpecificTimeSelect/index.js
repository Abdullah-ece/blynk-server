import React        from 'react';
import moment       from 'moment';
import {DatePicker} from 'antd';

class SpecificTimeSelect extends React.Component {

  static propTypes = {
    input: React.PropTypes.object,
    visible: React.PropTypes.bool,
    onChange: React.PropTypes.func,
    onCancel: React.PropTypes.func,
  };

  handleCancelClick() {
    this.props.onCancel();
  }

  handleChange(value) {
    if (!value.length)
      return this.props.input.onChange([]);

    this.props.input.onChange([
      value[0].unix() * 1000,
      value[1].unix() * 1000,
    ]);
  }

  handleOkClick(value) {
    if (!value.length)
      return this.props.input.onChange([]);

    this.props.input.onChange([
      value[0].unix() * 1000,
      value[1].unix() * 1000,
    ]);
  }

  render() {

    const value = this.props.input.value.length ? [
      moment(this.props.input.value[0]),
      moment(this.props.input.value[1]),
    ] : [];

    const disabledDate = (current) => {
      return current && current.valueOf() > Date.now();
    };

    const styles = {
      display: Boolean(this.props.visible) === true ? 'block' : 'none'
    };

    return (
      <div className="devices--device-timeline--time-filtering-specific-time-select" style={styles}>
        <DatePicker.RangePicker showTime={true}
                                disabledDate={disabledDate}
                                format="MMM D, YYYY hh:mm A"
                                value={value}
                                onChange={this.handleChange.bind(this)}
                                style={{width: '100%'}} onOk={this.handleOkClick.bind(this)}/>
      </div>
    );
  }

}

export default SpecificTimeSelect;
