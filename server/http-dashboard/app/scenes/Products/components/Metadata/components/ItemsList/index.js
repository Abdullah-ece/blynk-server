import React from 'react';
import classnames from 'classnames';

class ItemsList extends React.Component {
  static propTypes = {
    children: React.PropTypes.any,
    static: React.PropTypes.any
  };

  render() {

    const classNames = classnames({
      'product-metadata-items-list': !this.props.static,
      'product-metadata-items-list-static': this.props.static
    });

    return (
      <div className={classNames}>
        {this.props.children}
      </div>
    );
  }
}

export default ItemsList;
