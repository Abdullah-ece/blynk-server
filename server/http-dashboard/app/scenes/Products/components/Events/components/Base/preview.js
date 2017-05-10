import React from 'react';

class Preview extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="product-events-event-preview">{this.props.children}</div>
    );
  }

}

export default Preview;
