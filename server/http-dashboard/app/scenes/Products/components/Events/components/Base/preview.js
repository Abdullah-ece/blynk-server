import React from 'react';

class Preview extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div>{this.props.children}</div>
    );
  }

}

export default Preview;
