import React from 'react';
import {Icon} from "antd";
import FilteredBy from '../FilteredBy';
import PropTypes from 'prop-types';

class ByProduct extends React.Component {

  static propTypes = {
    devices: PropTypes.array,

    activeDeviceId: PropTypes.number,
    handleDeviceSelect: PropTypes.func,
  };

  render() {

    return (
      <FilteredBy icon={(<Icon type="environment-o"/>)}
                  othersLabel={'Unknown Locations'}
                  devices={this.props.devices}
                  activeDeviceId={this.props.activeDeviceId}
                  handleDeviceSelect={this.props.handleDeviceSelect}/>
    );
  }

}

export default ByProduct;
