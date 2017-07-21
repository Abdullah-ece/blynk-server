import React              from 'react';
import PropTypes          from 'prop-types';
import {Link}             from 'react-router';
import Dotdotdot          from 'react-dotdotdot';
import classnames         from 'classnames';

class MainItem extends React.Component {

  static propTypes = {
    link: PropTypes.string,
    name: PropTypes.string,
    logoUrl: PropTypes.string,
    noImageText: PropTypes.string,
    
    isActive: PropTypes.bool,
    devicesCount: PropTypes.number,
  };

  render() {

    const previewClassName = classnames({
      'main-list--item-preview': true,
      'main-list--item-preview--is-active': this.props.isActive !== false
    });

    return (
      <div className="main-list--item">
        <Link to={this.props.link}>
          <div className={previewClassName}>
            { this.props.isActive !== false ? ( this.props.logoUrl && (
              <img src={this.props.logoUrl}/>
            ) || (
              <div className="main-list--item-no-image">{this.props.noImageText || 'No Image'}</div>
            )) : (
              <div className="main-list--item-image-pending">{this.props.name.length && this.props.name.charAt(0)}</div>
            )}
          </div>
          <div className="main-list--item-details">
            <div className="main-list--item-details-name">
              <Dotdotdot clamp={1}>{ this.props.name }</Dotdotdot>
            </div>
            <div className="main-list--item-details-amount">
              { this.props.isActive !== false ? (
                `${this.props.devicesCount || 0} Devices`
              ) : (
                `Pending`
              )}
            </div>
          </div>
        </Link>
      </div>
    );
  }

}

export default MainItem;
