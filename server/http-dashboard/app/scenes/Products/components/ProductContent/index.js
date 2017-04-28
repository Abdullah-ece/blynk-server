import React from 'react';

class ProductHeader extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    style: React.PropTypes.string
  };

  render() {
    return (
      <div className="products-content" style={this.props.style}>
        { this.props.children }
      </div>
    );
  }
}

export default ProductHeader;
