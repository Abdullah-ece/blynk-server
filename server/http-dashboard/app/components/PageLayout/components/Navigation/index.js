import React from 'react';

class Navigation extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="page-layout-navigation-inner">{this.props.children}</div>
    );
  }

}

export default Navigation;
