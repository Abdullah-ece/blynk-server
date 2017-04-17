import React from 'react';

class Name extends React.Component {
  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="product-metadata-item--preview--unavailable">No Preview available</div>
    );
  }
}

export default Name;
