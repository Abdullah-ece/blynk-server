import React, { Component } from 'react';
import { DeviceDeleteComponent } from 'scenes/Devices/components';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import {
  DeviceDelete as handleDeviceDelete,
  DevicesFetch
} from 'data/Devices/api';
import { VerifyPermission, PERMISSIONS_INDEX } from "services/Roles";

@connect((state) => ({
  organization: state.Organization,
  permissions: state.RolesAndPermissions.currentRole.permissionGroup1,
}), (dispatch) => ({
  fetchDevices: bindActionCreators(DevicesFetch, dispatch),
  handleDeviceDelete: bindActionCreators(handleDeviceDelete, dispatch),
}))
class DeviceDelete extends Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    deviceId: React.PropTypes.number,
    organization: React.PropTypes.object,
    fetchDevices: React.PropTypes.func,
    onDeviceDelete: React.PropTypes.func,
    handleDeviceDelete: React.PropTypes.func,
    permissions: React.PropTypes.number,
  };

  constructor(props) {
    super(props);

    this.handleDelete = this.handleDelete.bind(this);
  }

  handleDelete(deviceId) {
    this.props.onDeviceDelete(deviceId);
  }

  render() {

    return (
      <DeviceDeleteComponent deviceId={this.props.deviceId}
                             onDelete={this.handleDelete}
                             orgId={this.props.organization.id}
                             canDeleteDevice={VerifyPermission(this.props.permissions, PERMISSIONS_INDEX.ORG_DEVICES_DELETE)}/>
    );
  }
}

export default DeviceDelete;
