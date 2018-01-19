import React from 'react';
import {Select} from 'antd';

class DeviceSelect extends React.Component {

  render() {
    return (
      <Select value={`1`}>
        <Select.Option key={`1`}>Test Device 1</Select.Option>
        <Select.Option key={`2`}>Test Device 2</Select.Option>
        <Select.Option key={`3`}>Test Device 3</Select.Option>
        <Select.Option key={`4`}>Test Device 4</Select.Option>
      </Select>
    );
  }

}

export default DeviceSelect;
