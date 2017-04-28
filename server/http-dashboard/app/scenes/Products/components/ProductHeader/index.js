import React from 'react';

class ProductHeader extends React.Component {

  static propTypes = {
    title: React.PropTypes.oneOfType([
      React.PropTypes.string,
      React.PropTypes.object
    ]),
    options: React.PropTypes.object
  };

  render() {
    return (
      <div className="products-header">
        <div className="products-header-name">
          {this.props.title}
        </div>
        <div className="products-header-options">
          {this.props.options}
        </div>
      </div>
    );
  }
}

export default ProductHeader;
