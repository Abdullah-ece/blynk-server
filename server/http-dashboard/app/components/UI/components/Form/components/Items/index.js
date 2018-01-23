import React from 'react';
import classNames from 'classnames';

import './styles.less';

export default class Items extends React.Component {
  static propTypes = {
    children: React.PropTypes.any,
    offset: React.PropTypes.any,
    layout: React.PropTypes.string,
    className: React.PropTypes.string
  };

  render() {

    const className = classNames({
      'ui-form-items': true,
      'none-offset': this.props.offset === false,
      [`${this.props.className}`]: !!this.props.className,
      [`${this.props.offset}-offset`]: !!this.props.offset,
      [`${this.props.layout}-layout`]: !!this.props.layout,
    });

    return (
      <div className={className}>
        { this.props.children }
      </div>
    );
  }
}
