import React from 'react';
import { Input as BaseInput, Icon, Form } from 'antd';

class DeviceAuthTokenModal extends React.Component {

  prefix(icon) {
    return (icon ? <Icon type={icon} className="form--field-icon"/> : null);
  }

  render() {
    const { placeholder, disabled, rows, input, type, icon, autoComplete, error } = this.props;

    let validateStatus = 'success';
    let help = '';

    if (error) {
      validateStatus = 'error';
      help = error;
    }

    return (
      <Form.Item validateStatus={validateStatus}
                 help={help}>
        <BaseInput {...input}
                   disabled={disabled}
                   rows={rows}
                   autoComplete={autoComplete}
                   type={type}
                   onChange={(value) => {
                     input.onChange(value);
                   }}
                   placeholder={placeholder}
                   prefix={this.prefix(icon)}
        />
      </Form.Item>
    );
  }

}

export default DeviceAuthTokenModal;
