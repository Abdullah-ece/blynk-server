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

  render() {
    const className = classnames({
      'navigation-devices-list-item': true,
      'navigation-devices-list-item-active': this.props.active
    });

    return (
      <div className={className} onClick={this.props.onClick}>
        <div className="navigation-devices-list-item-inner">
          <div className="navigation-devices-list-item-name">
            <Dotdotdot clamp={1}>{this.props.device.get('name')}</Dotdotdot>
          </div>
          <div className="navigation-devices-list-item-product-name">
            <Dotdotdot clamp={1}>{this.props.device.has('product.name') && this.props.device.getIn('product.name') || (
              <i>No Product Name</i>) }</Dotdotdot>
          </div>
          <div className="navigation-devices-list-item-events">
            { this.props.device.has('critical') && (
              <Badge count={this.props.device.get('critical')} className="critical"/>
            )}
            { !this.props.device.has('critical') && this.props.device.has('warning') && (
              <Badge count={this.props.device.get('warning')} className="warning"/>
            )}
          </div>
        </div>
      </div>
    );
  }

}

export default DeviceItem;
