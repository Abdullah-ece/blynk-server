import React from 'react';

class Content extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="page-layout-content-inner">{this.props.children}</div>
    );
  }

}

export default Content;
