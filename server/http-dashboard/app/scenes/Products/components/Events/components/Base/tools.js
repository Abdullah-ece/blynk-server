import React from 'react';

class Tools extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="product-events-event-tools">{this.props.children}</div>
    );
  }

}

export default Tools;
