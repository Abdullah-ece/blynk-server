import React from 'react';
import {Header} from './components';
import './styles.less';

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

Content.Header = Header;

export default Content;
