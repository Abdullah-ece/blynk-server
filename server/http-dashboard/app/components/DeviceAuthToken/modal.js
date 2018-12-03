import React from 'react';
import { Input as BaseInput, Icon, Form } from 'antd';
import PropTypes from "prop-types";

class DeviceAuthTokenModal extends React.Component {
  static propTypes = {
    icon: PropTypes.string,
    input: PropTypes.object,
    error: PropTypes.string,
    type: PropTypes.string,
    disabled: PropTypes.bool,
    placeholder: PropTypes.string,
  };

  prefix(icon) {
    return (icon ? <Icon type={icon} className="form--field-icon"/> : null);
  }

  render() {
    const { placeholder, disabled, input, type, icon, error } = this.props;

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
