import React, {Component} from 'react';
import {DeviceDeleteComponent} from 'scenes/Devices/components';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {DeviceDelete as handleDeviceDelete} from 'data/Devices/api';

@connect((state) => ({
  organization: state.Organization
}), (dispatch) => ({
  handleDeviceDelete: bindActionCreators(handleDeviceDelete, dispatch),
}))
class DeviceDelete extends Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    deviceId: React.PropTypes.number,
    organization: React.PropTypes.object,
    handleDeviceDelete: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleDelete = this.handleDelete.bind(this);
  }

  handleDelete(deviceId, orgId) {
    this.props.handleDeviceDelete(deviceId,orgId);
    this.context.router.push('/devices');
  }

  render() {

    return (
       <DeviceDeleteComponent deviceId = {this.props.deviceId}
                              onDelete = {this.handleDelete}
                              orgId={this.props.organization.id}/>
    );
  }
}

export default DeviceDelete;
