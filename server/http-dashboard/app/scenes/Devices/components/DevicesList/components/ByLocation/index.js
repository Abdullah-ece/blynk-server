import React from 'react';
import {Icon} from "antd";
import {List} from "immutable";
import DeviceItem from '../DeviceItem';

class ByLocation extends React.Component {

  static propTypes = {
    devices: React.PropTypes.instanceOf(List),

    isActive: React.PropTypes.func,
    handleDeviceSelect: React.PropTypes.func,
  };

  render() {
    return (
      <div className="navigation-devices-list">
        {this.props.devices && this.props.devices.map((group, key) => (
          <div className="navigation-devices-list-items" key={key}>
            {group && !group.get('isOthers') && (
              <div className="navigation-devices-list-items--title">
                <Icon type="environment-o"/> {group.get('name')}
              </div>
            )}

            {group && group.get('isOthers') && group.get('items').size && (
              <div className="navigation-devices-list-items--title--others">
                Other devices
              </div>
            ) || null}

            <div className="navigation-devices-list-items--content">
              {group && group.get('items').size && group.get('items').map((device) => (
                <DeviceItem key={device.get('id')}
                            device={device}
                            active={this.props.isActive(device)}
                            onClick={this.props.handleDeviceSelect}
                />
              )) || null}
            </div>
          </div>
        ))}

      </div>
    );
  }

}

export default ByLocation;
