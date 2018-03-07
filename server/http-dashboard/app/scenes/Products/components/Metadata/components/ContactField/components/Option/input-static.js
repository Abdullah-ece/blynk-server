import React from 'react';
import Default from './default';
import FieldStub from 'scenes/Products/components/FieldStub';

export default class Input extends Default.Static {

  static propTypes = {
    placeholder: React.PropTypes.string,
    isChecked: React.PropTypes.any
  };

  render() {

    if (!this.props.isChecked) return null;
    return (
      <FieldStub inline={true}>{ this.props.value || this.props.placeholder }</FieldStub>
    );
  }

}
