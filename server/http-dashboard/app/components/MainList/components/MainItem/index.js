import React              from 'react';
import {Icon}             from 'antd';
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

    isChecked: PropTypes.bool,
    lightOverlay: PropTypes.bool,
    isActive: PropTypes.bool,

    id: PropTypes.number,
    devicesCount: PropTypes.number,

    onItemClick: PropTypes.func,

    item: PropTypes.object
  };

  constructor(props) {
    super(props);

    this.handleClick = this.handleClick.bind(this);
  }

  handleClick() {
    if (this.props.onItemClick && this.props.item)
      this.props.onItemClick(this.props.item);
  }

  render() {

    const previewClassName = classnames({
      'main-list--item-preview': true,
      'main-list--item-preview--is-active': this.props.isActive !== false
    });

    const className = classnames({
      'main-list--item': true,
      'main-list--item--checked': this.props.isChecked === true,
      'main-list--item--overlay': this.props.lightOverlay === true && !this.props.isChecked
    });

    return (
      <div className={className} onClick={this.handleClick}>
        { this.props.isChecked && (
          <div className="main-list--item-check-icon">
            <Icon type="check-circle"/>
          </div>
        )}
        <Link to={this.props.link}>
          <div className={previewClassName}>
            { this.props.logoUrl && (
              <img src={this.props.logoUrl}/>
            ) || (
              <div className="main-list--item-image-pending">{this.props.name.length && this.props.name.charAt(0)}</div>
            )}
          </div>
          <div className="main-list--item-details">
            <div className="main-list--item-details-name">
              <Dotdotdot clamp={1}>{ this.props.name }</Dotdotdot>
            </div>
            <div className="main-list--item-details-amount">
              { this.props.isActive !== false ? (
                `${this.props.devicesCount || "No"} Device${this.props.devicesCount === 1 ? "":"s"}`
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
