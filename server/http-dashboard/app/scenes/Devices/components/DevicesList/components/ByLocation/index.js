import React from 'react';
import {Icon} from "antd";
import {List} from "immutable";
import FilteredBy from '../FilteredBy';

class ByProduct extends React.Component {

  static propTypes = {
    devices: React.PropTypes.instanceOf(List),

    isActive: React.PropTypes.func,
    handleDeviceSelect: React.PropTypes.func,
  };

  render() {

    return (
      <FilteredBy icon={(<Icon type="environment-o"/>)}
                  othersLabel={'Unknown Locations'}
                  devices={this.props.devices}
                  isActive={this.props.isActive}
                  handleDeviceSelect={this.props.handleDeviceSelect}/>
    );
  }

}

export default ByProduct;
