import React from 'react';
import Dotdotdot from 'react-dotdotdot';
import {Badge} from 'antd';
import classnames from 'classnames';
import {Map} from 'immutable';

class DeviceItem extends React.Component {

  static propTypes = {
    device: React.PropTypes.instanceOf(Map),
    onClick: React.PropTypes.func,
    active: React.PropTypes.bool
  };

  constructor(props) {
    super(props);

    this.handleDeviceClick = this.handleDeviceClick.bind(this);
  }

  handleDeviceClick() {
    this.props.onClick(this.props.device);
  }

  render() {
    const className = classnames({
      'navigation-devices-list-item': true,
      'navigation-devices-list-item-active': this.props.active
    });

    return (
      <div className={className} onClick={this.handleDeviceClick}>
        <div className="navigation-devices-list-item-inner">
          <div className="navigation-devices-list-item-name">
            <Dotdotdot clamp={1}>{this.props.device.get('name')}</Dotdotdot>
          </div>
          <div className="navigation-devices-list-item-product-name">
            <Dotdotdot clamp={1}>{this.props.device.has('productName') && this.props.device.get('productName') || (
              <i>No Product Name</i>)}</Dotdotdot>
          </div>
          <div className="navigation-devices-list-item-events">
            {this.props.device.has('criticalSinceLastView') && (
              <Badge count={this.props.device.get('criticalSinceLastView')} className="critical"/>
            )}
            {!this.props.device.has('criticalSinceLastView') && this.props.device.has('warningSinceLastView') && (
              <Badge count={this.props.device.get('warningSinceLastView')} className="warning"/>
            )}
          </div>
        </div>
      </div>
    );
  }

}

export default DeviceItem;
