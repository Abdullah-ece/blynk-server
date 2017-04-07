import React from 'react';

import Content from './components/Content';
import Header from './components/Header';
import Menu from './components/Menu';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as AccountActions from 'data/Account/actions';

import './styles.scss';

@connect(() => ({}), (dispatch) => ({
  fetchAccount: bindActionCreators(AccountActions.Account, dispatch)
}))
class UserLayout extends React.Component {

  static propTypes = {
    children: React.PropTypes.object,
    fetchAccount: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    props.fetchAccount();

  }

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
