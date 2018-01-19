import React from 'react';
import {Select} from 'antd';
import PropTypes from 'prop-types';

class DeviceSelect extends React.Component {

  static propTypes = {

    value: PropTypes.string,

    devicesList: PropTypes.array,

    onChange: PropTypes.func,
  };

  render() {

    const { value, onChange} = this.props;

    let devicesList = this.props.devicesList.map((device) => ({
      key: device.id,
      value: device.name,
    }));

    return (
      <Select value={value} onChange={onChange} placeholder={`Select device for preview`}>
        { devicesList.map((device) => (
          <Select.Option key={`${device.key}`}>{device.value}</Select.Option>
        ))}
      </Select>
    );
  }

}

export default DeviceSelect;
