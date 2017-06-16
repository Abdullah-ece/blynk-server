import React from 'react';
import {Link} from 'react-router';
import DeviceCreateModal from './../DeviceCreateModal';
import {Button} from 'antd';
import './styles.less';

class NoDevices extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  state = {
    isDeviceCreateModalVisible: false
  };

  componentDidUpdate() {
    this.checkModalVisibility();
  }

  componentWillMount() {
    this.checkModalVisibility();
  }

  onDeviceCreateModalClose() {
    this.context.router.push('/devices');
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
      <div className="devices">
        <div className="devices-no-items">
          <div className="devices-no-items-title">
            This is the place to manage all your devices
          </div>
          { !this.props.isAnyProductExist && (
            <div>
              <div className="devices-no-items-description">
                Here you will find a list of all of your activated devices and their data visualized.
                You will be able to edit their data and track important events.
                <br/>
                <br/>
                You need to have at least one Product set up before adding new devices.
              </div>
              <div className="devices-no-items-action">
                <Link to="/products/create">
                  <Button icon="plus" type="primary">Create New Product</Button>
                </Link>
              </div>
            </div>
          ) || (
            <div>
              <div className="devices-no-items-description">
                Here you will find a list of all of your activated devices and their data visualized.
                You will be able to edit their data and track important events.
              </div>
              <div className="devices-no-items-action">
                <Link to="/devices/create">
                  <Button icon="plus" type="primary">Create New Device</Button>
                </Link>
              </div>

              <DeviceCreateModal visible={this.state.isDeviceCreateModalVisible}
                                 onClose={this.onDeviceCreateModalClose.bind(this)}/>

            </div>
          )}

        </div>
      </div>
    );
  }

}

export default NoDevices;
