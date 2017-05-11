import React from 'react';
import {Checkbox as NativeCheckbox} from 'antd';

class Checkbox extends React.Component {

  static propTypes = {
    input: React.PropTypes.object,
    placeholder: React.PropTypes.string
  };

  shouldComponentUpdate(nextProps) {
    return this.props.input.value !== nextProps.input.value || this.props.placeholder !== nextProps.placeholder;
  }

  render() {
    return (
      <NativeCheckbox onChange={this.props.input.onChange}
                      checked={!!this.props.input.value}>{this.props.placeholder}</NativeCheckbox>
    );
  }

}

export default Checkbox;
