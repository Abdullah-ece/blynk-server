import React from 'react';
import './styles.less';

class Header extends React.Component {

  static propTypes = {
    title: React.PropTypes.string
  };

  render() {
    return (
      <div className="page-layout-content-header">
        {this.props.title}
      </div>
    );
  }

}

export default Header;
