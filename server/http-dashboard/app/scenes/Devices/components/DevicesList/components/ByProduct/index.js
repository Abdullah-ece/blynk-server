import React from 'react';
import {Icon} from "antd";
import FilteredBy from '../FilteredBy';
import PropTypes from 'prop-types';
import _ from 'lodash';

class ByProduct extends React.Component {

  static propTypes = {
    devices: PropTypes.array,

    activeDeviceId: PropTypes.number,
    handleDeviceSelect: PropTypes.func,
  };

  shouldComponentUpdate(nextProps) {
    return (
      !_.isEqual(nextProps.devices, this.props.devices) ||
      !_.isEqual(nextProps.activeDeviceId, this.props.activeDeviceId)
    );
  }

  render() {

    return (
      <FilteredBy icon={(<Icon type="appstore-o"/>)}
                  othersLabel={'Devices with no Products assigned'}
                  devices={this.props.devices}
                  activeDeviceId={this.props.activeDeviceId}
                  handleDeviceSelect={this.props.handleDeviceSelect}/>
    );
  }

}

export default ByProduct;
