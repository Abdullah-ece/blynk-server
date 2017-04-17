import React from 'react';

class Name extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="product-metadata-item--preview--value">{this.props.children}</div>
    );
  }
}

export default Name;
