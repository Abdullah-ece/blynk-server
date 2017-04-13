import React from 'react';
import Menu from '../Menu';
import Content from '../Content';

import './styles.less';

class UserProfileLayout extends React.Component {

  static propTypes = {
    children: React.PropTypes.object,
  };

  render() {
    return (
      <div className="user-profile-container">
        <Menu/>
        <Content>
          { this.props.children }
        </Content>
      </div>
    );
  }
}

export default UserProfileLayout;
