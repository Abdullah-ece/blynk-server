import React from 'react';

class Name extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="product-metadata-item--preview--name">{this.props.children.trim()}{'\u00A0'}</div>
    );
  }
}

export default Name;
