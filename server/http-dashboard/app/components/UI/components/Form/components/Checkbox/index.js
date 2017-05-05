import React from 'react';
import {Checkbox as BaseCheckbox} from 'antd';

export default class Checkbox extends React.Component {

  static propTypes = {
    input: React.PropTypes.object
  };

  render() {
    const {input} = this.props;
    return (
      <BaseCheckbox onChange={input.onChange} value={input.value}/>
    );
  }

}
