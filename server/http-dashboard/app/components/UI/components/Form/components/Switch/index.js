import React from 'react';
import {Switch as BaseSwitch} from 'antd';

export default class Switch extends React.Component {

  static propTypes = {
    input: React.PropTypes.any,
  };

  render() {
    const {input} = this.props;
    return (
      <BaseSwitch onChange={input.onChange} checked={input.value}/>
    );
  }

}
