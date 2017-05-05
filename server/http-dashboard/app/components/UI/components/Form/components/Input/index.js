import React from 'react';
import {Input as BaseInput, Icon} from 'antd';

export default class Input extends React.Component {

  prefix(icon) {
    return icon ? <Icon type={icon} className="form--field-icon"/> : null;
  }

  render() {
    const {placeholder, rows, input, type, icon} = this.props;
    return (
      <BaseInput {...input}
                 rows={rows}
                 type={type}
                 placeholder={placeholder}
                 prefix={ this.prefix(icon) }
      />
    );
  }

}
