import React from 'react';
import Header from '../Header';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as AccountActions from '../../../../data/Account/actions';

import './styles.less';

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
        { this.props.children }
      </div>
    );
  }

}

export default UserLayout;
