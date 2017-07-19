import React              from 'react';
import PropTypes          from 'prop-types';
import {Link}             from 'react-router';
import Dotdotdot          from 'react-dotdotdot';

class MainItem extends React.Component {

  static propTypes = {
    link: PropTypes.string,
    name: PropTypes.string,
    logoUrl: PropTypes.string,
    devicesCount: PropTypes.number,
  };

  render() {
    return (
      <div className="main-list--item">
        <Link to={this.props.link}>
          <div className="main-list--item-preview">
            { this.props.logoUrl && (
              <img src={this.props.logoUrl}/>
            ) || (
              <div className="product-item-no-image">No Product Image</div>
            )}
          </div>
          <div className="main-list--item-details">
            <div className="main-list--item-details-name">
              <Dotdotdot clamp={1}>{ this.props.name }</Dotdotdot>
            </div>
            <div className="main-list--item-details-amount">
              { this.props.devicesCount || 0 } Devices
            </div>
          </div>
        </Link>
      </div>
    );
  }

}

export default MainItem;
