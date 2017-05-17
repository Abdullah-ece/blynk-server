import React from 'react';
import Dotdotdot from 'react-dotdotdot';
import {Badge} from 'antd';
import classnames from 'classnames';

class DeviceItem extends React.Component {

  static propTypes = {
    name: React.PropTypes.string,
    productName: React.PropTypes.string,
    critical: React.PropTypes.number,
    warning: React.PropTypes.number,
    active: React.PropTypes.bool
  };

  render() {

    const className = classnames({
      'navigation-devices-list-item': true,
      'navigation-devices-list-item-active': this.props.active
    });

    return (
      <div className={className}>
        <div className="navigation-devices-list-item-name">
          <Dotdotdot clamp={1}>{this.props.name}</Dotdotdot>
        </div>
        <div className="navigation-devices-list-item-product-name">
          <Dotdotdot clamp={1}>{this.props.productName}</Dotdotdot>
        </div>
        <div className="navigation-devices-list-item-events">
          { !!this.props.critical && (
            <Badge count={this.props.critical} className="critical"/>
          )}
          { !this.props.critical && !!this.props.warning && (
            <Badge count={this.props.warning} className="warning"/>
          )}
        </div>
      </div>
    );
  }

}

export default DeviceItem;
