import React from 'react';
import {Checkbox} from 'antd';
import FieldStub from 'scenes/Products/components/FieldStub';

export default class Default extends React.Component {

  static propTypes = {
    placeholder: React.PropTypes.string,
    prefix: React.PropTypes.string,
    isChecked: React.PropTypes.any,
  };

  capitalizePrefix(name) {
    return name[0].toUpperCase() + name.substr(1);
  }

  checkbox(props) {
    if (props.placeholder) {
      return (
        <Checkbox disabled={true} checked={!!props.isChecked}>{props.placeholder}</Checkbox>
      );
    } else {
      return (
        <Checkbox disabled={true} checked={!!props.isChecked}/>
      );
    }
  }

  render() {
    if (!this.props.isChecked) return null;
    return (
      <FieldStub inline={true}>{ this.props.placeholder }</FieldStub>
    );
  }

}
