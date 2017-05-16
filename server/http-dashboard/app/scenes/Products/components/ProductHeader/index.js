import React from 'react';
import Dotdotdot from 'react-dotdotdot';

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
          <Dotdotdot clamp={1}>
            {this.props.title}
          </Dotdotdot>
        </div>
        <div className="products-header-options">
          {this.props.options}
        </div>
      </div>
    );
  }
}

export default ProductHeader;
