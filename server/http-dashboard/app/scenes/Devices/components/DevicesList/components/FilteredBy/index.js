import React from 'react';
import {Collapse} from "antd";
import DeviceItem from '../DeviceItem';
import PropTypes from 'prop-types';

class FilteredBy extends React.Component {

  static propTypes = {
    devices: PropTypes.array,

    othersLabel: PropTypes.string,

    activeDeviceId: PropTypes.number,

    handleDeviceSelect: PropTypes.func,

    icon: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.element
    ]),
  };

  render() {

    return (
      <div className="navigation-devices-list navigation-devices-list-collapsed">
        <Collapse className="no-styles">

          {this.props.devices && this.props.devices.map((group) => {

            if (!group.items || !group.items.length)
              return null;

            let header = '';

            if (group.isOthers) {
              header = (<span>{this.props.othersLabel}</span>);
            } else {
              header = (<div>{this.props.icon || null} {group.name}</div>);
            }

            return (
              <Collapse.Panel header={header} key={group.name}>

                <div className="navigation-devices-list-items--content">
                  {group && group.items && group.items.length && group.items.map((device) => (
                    <DeviceItem key={device.id}
                                device={device}
                                active={this.props.activeDeviceId === device.id}
                                onClick={this.props.handleDeviceSelect}
                    />
                  )) || null}
                </div>

              </Collapse.Panel>
            );

          })}

        </Collapse>
      </div>
    );
  }

}

export default FilteredBy;
