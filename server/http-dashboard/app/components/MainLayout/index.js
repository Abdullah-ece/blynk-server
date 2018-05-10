import React      from 'react';
import {
  Header,
  Content
}                 from './components';
import PropTypes  from 'prop-types';
import './styles.less';

class MainLayout extends React.Component {

  static propTypes = {
    children: PropTypes.any,
  };

  render() {
    return (
      <div className="main-layout">
        { this.props.children }
      </div>
    );
  }

}

MainLayout.Header = Header;
MainLayout.Content = Content;

export default MainLayout;
