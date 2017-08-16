import React from 'react';
import {Collapse} from "antd";
import {List} from "immutable";
import DeviceItem from '../DeviceItem';
import PropTypes from 'prop-types';

class FilteredBy extends React.Component {

  static propTypes = {
    devices: PropTypes.instanceOf(List),

    othersLabel: PropTypes.string,

    isActive: PropTypes.func,
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

            if (!group.get('items').size)
              return null;

            let header = '';

            if (group.get('isOthers')) {
              header = (<span>{this.props.othersLabel}</span>);
            } else {
              header = (<div>{this.props.icon || null} {group.get('name')}</div>);
            }

            return (
              <Collapse.Panel header={header} key={group.get('name')}>

                <div className="navigation-devices-list-items--content">
                  {group && group.get('items').size && group.get('items').map((device) => (
                    <DeviceItem key={device.get('id')}
                                device={device}
                                active={this.props.isActive(device)}
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
