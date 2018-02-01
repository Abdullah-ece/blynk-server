import React from 'react';
import {Select} from 'antd';
import PropTypes from 'prop-types';
import {List} from 'immutable';

class DeviceSelect extends React.Component {

  static propTypes = {

    value: PropTypes.number,

    devicesList: PropTypes.instanceOf(List),

    onChange: PropTypes.func,
  };

  render() {

    const { value, onChange} = this.props;

    let devicesList = this.props.devicesList.map((device) => ({
      key: device.get('id'),
      value: device.get('name'),
    })).toJS();

    return (
      <Select value={value ? String(value) : undefined} onChange={onChange} placeholder={`Select device for preview`}>
        { devicesList.map((device) => (
          <Select.Option key={`${device.key}`} value={`${device.key}`}>{device.value}</Select.Option>
        ))}
      </Select>
    );
  }

}

export default DeviceSelect;
