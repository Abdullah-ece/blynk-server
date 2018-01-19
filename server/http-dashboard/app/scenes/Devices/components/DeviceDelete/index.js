import React, {Component} from 'react';
import {Popconfirm, Button} from 'antd';
import './styles.less';

class DeviceDelete extends Component {
  static propTypes = {
    deviceId: React.PropTypes.number,
    orgId: React.PropTypes.number,
    onDelete: React.PropTypes.func,
  };
  constructor(props) {
    super(props);
    this.handleDeviceDelete = this.handleDeviceDelete.bind(this);
  }

  handleDeviceDelete() {
    this.props.onDelete(this.props.deviceId, this.props.orgId);
  }

  render() {
    return (
       <div className={"device-delete-component"}>
         <Popconfirm title="Are you sure you want to delete this device?"
                          okText="Yes"
                          cancelText="No"
                          onConfirm={this.handleDeviceDelete}
                          overlayClassName="danger">
           <Button type="danger">Delete</Button>
         </Popconfirm>
       </div>
    );
  }
}

export default DeviceDelete;
