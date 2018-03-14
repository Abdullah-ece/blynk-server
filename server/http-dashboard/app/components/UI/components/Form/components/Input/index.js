import React from 'react';
import {Input as BaseInput, Icon} from 'antd';

export default class Input extends React.Component {

  static propTypes = {
    rows: React.PropTypes.any,
    type: React.PropTypes.any,
    icon: React.PropTypes.any,
    input: React.PropTypes.any,
    suffix: React.PropTypes.any,
    disabled: React.PropTypes.any,
    isChecked: React.PropTypes.any,
    placeholder: React.PropTypes.any,
    autoComplete: React.PropTypes.any,
  };

  prefix(icon) {
    return (icon ? <Icon type={icon} className="form--field-icon"/> : null);
  }

  render() {
    const {placeholder, disabled, rows, input, type, icon, autoComplete} = this.props;
    return (
      <BaseInput {...input}
                 disabled={disabled}
                 rows={rows}
                 autoComplete={autoComplete}
                 type={type}
                 placeholder={placeholder}
                 prefix={this.prefix(icon)}
                 suffix={this.props.suffix}
      />
    );
  }

}
