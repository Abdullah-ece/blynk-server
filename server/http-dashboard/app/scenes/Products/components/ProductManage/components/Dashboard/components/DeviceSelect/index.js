import React from 'react';
import {Select} from 'antd';
import PropTypes from 'prop-types';

class DeviceSelect extends React.Component {

  static propTypes = {

    value: PropTypes.string,

    devicesList: PropTypes.arrayOf(PropTypes.shape({
      id: PropTypes.string,
      value: PropTypes.string
    })),

    onChange: PropTypes.func,
  };

  render() {

    const { value, devicesList, onChange} = this.props;

    return (
      <Select value={value} onChange={onChange} placeholder={`Select device for preview`}>
        { devicesList.map((device) => (
          <Select.Option key={device.key}>{device.value}</Select.Option>
        ))}
      </Select>
    );
  }

}

export default DeviceSelect;
