import React  from 'react';
import {
  Switch as BaseSwitch
}             from 'antd';

export default class Switch extends React.Component {

  static propTypes = {
    input: React.PropTypes.any,
    label: React.PropTypes.string,

    element: React.PropTypes.object,
  };

  render() {
    const {input} = this.props;
    return (
      <div>
        <BaseSwitch size={this.props.element.props.size} onChange={input.onChange}
                    checked={Boolean(input.value)}/> {this.props.element.props.size && (
        <span>{this.props.element.props.label}</span>
      )}
      </div>
    );
  }

}
