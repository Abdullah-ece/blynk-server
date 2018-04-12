import React from 'react';
import Dotdotdot from 'react-dotdotdot';
import {Badge} from 'antd';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import {onlyUpdateForKeys} from 'recompose';

@onlyUpdateForKeys(['device', 'active'])
class DeviceItem extends React.Component {

  static propTypes = {
    device: PropTypes.shape({
      id: PropTypes.number,
      name: PropTypes.string,
      productName: PropTypes.string,
      criticalSinceLastView: PropTypes.number,
      warningSinceLastView: PropTypes.number,
      status: PropTypes.oneOf(['ONLINE', 'OFFLINE']),
    }),
    onClick: PropTypes.func,
    active: PropTypes.bool
  };

  constructor(props) {
    super(props);

    this.handleDeviceClick = this.handleDeviceClick.bind(this);
  }

  // shouldComponentUpdate(nextProps) {
  //   return (
  //     !_.isEqual(nextProps.device, this.props.device) ||
  //     !_.isEqual(nextProps.active, this.props.active)
  //   );
  // }

  handleDeviceClick() {
    this.props.onClick(this.props.device);
  }

  render() {
    const className = classnames({
      'navigation-devices-list-item': true,
      'navigation-devices-list-item-active': this.props.active,
      'navigation-devices-list-item-online': this.props.device.status === 'ONLINE',
      'navigation-devices-list-item-offline': this.props.device.status === 'OFFLINE',
    });

    return (
      <div className={className} onClick={this.handleDeviceClick}>
        <div className="navigation-devices-list-item-inner">
          <div className="navigation-devices-list-item-name">
            <Dotdotdot clamp={1}>{this.props.device.name}</Dotdotdot>
          </div>
          <div className="navigation-devices-list-item-product-name">
            <Dotdotdot clamp={1}>{this.props.device.productName || (
              <i>No Product Name</i>)}</Dotdotdot>
          </div>
          <div className="navigation-devices-list-item-events">
            {this.props.device.criticalSinceLastView && (
              <Badge count={this.props.device.criticalSinceLastView} className="critical"/>
            )}
            {!this.props.device.criticalSinceLastView && this.props.device.warningSinceLastView && (
              <Badge count={this.props.device.warningSinceLastView} className="warning"/>
            )}
          </div>
        </div>
      </div>
    );
  }

}

export default DeviceItem;
