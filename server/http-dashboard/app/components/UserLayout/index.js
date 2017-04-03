import React from 'react';

import Content from './components/Content';
import Header from './components/Header';
import Menu from './components/Menu';

import './styles.scss';

class UserLayout extends React.Component {

  static propTypes = {
    children: React.PropTypes.object
  };

  render() {

    return (
      <div className="user-layout">
        <Header />
        <div className="user-layout-container">
          <Menu/>
          <Content>
            { this.props.children }
          </Content>
        </div>
      </div>
    );
  }

}

export default UserLayout;
