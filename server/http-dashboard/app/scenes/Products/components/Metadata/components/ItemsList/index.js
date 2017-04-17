import React from 'react';

class ItemsList extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="product-metadata-items-list">
        {this.props.children}
      </div>
    );
  }
}

export default ItemsList;
