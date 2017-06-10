import React from 'react';
import {Button, Tooltip} from 'antd';
import DeviceCreateModal from '../DeviceCreateModal';
import './styles.less';

class DevicesToolbar extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object,
  };

  static propTypes = {
    location: React.PropTypes.object,
    params: React.PropTypes.object
  };

  state = {
    isDeviceCreateModalVisible: false
  };

  componentWillMount() {
    this.checkModalVisibility();
  }

  shouldComponentUpdate(nextProps, nextState) {
    return (
      this.props.location.pathname !== nextProps.location.pathname ||
      this.state.isDeviceCreateModalVisible !== nextState.isDeviceCreateModalVisible
    );
  }

  componentDidUpdate() {
    this.checkModalVisibility();
  }

  onDeviceCreateModalClose() {
    this.context.router.push(`/devices/${this.props.params.id}`);
  }

  handleDeviceCreateClick() {
    this.context.router.push(`/devices/${this.props.params.id}/create`);
  }

  checkModalVisibility() {
    if (this.props.location.pathname.indexOf('create') !== -1 && !this.state.isDeviceCreateModalVisible) {
      this.setState({
        isDeviceCreateModalVisible: true
      });
    } else if (this.props.location.pathname.indexOf('create') === -1 && this.state.isDeviceCreateModalVisible) {
      this.setState({
        isDeviceCreateModalVisible: false
      });
    }
  }

  render() {
    return (
      <div className="devices--toolbar">
        <Tooltip placement="bottomRight" title="Create new device">
          <Button icon="plus-square-o" size="small" onClick={this.handleDeviceCreateClick.bind(this)}/>
        </Tooltip>
        <Tooltip placement="bottomRight" title="Device Location">
          <Button icon="global" size="small" disabled={true}/>
        </Tooltip>
        <Tooltip placement="bottomRight" title="Device Owner">
          <Button icon="user" size="small" disabled={true}/>
        </Tooltip>
        <Tooltip placement="bottom" title="Global Filter">
          <Button icon="filter" size="small" disabled={true}/>
        </Tooltip>

        <DeviceCreateModal visible={this.state.isDeviceCreateModalVisible}
                           onClose={this.onDeviceCreateModalClose.bind(this)}/>

      </div>
    );
  }

}

export default DevicesToolbar;
