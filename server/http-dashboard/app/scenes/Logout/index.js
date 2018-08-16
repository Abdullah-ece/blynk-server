import React from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';

import {AccountClearCredentials} from "data/Account/actions";
import {LoginWsLogout} from "data/Login/actions";

@connect(() => {
  return {};
}, (dispatch) => {
  return {
    LoginWsLogout: bindActionCreators(LoginWsLogout, dispatch),
    AccountClearCredentials: bindActionCreators(AccountClearCredentials, dispatch),
  };
})
export default class Logout extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    LoginWsLogout: React.PropTypes.func,
    AccountClearCredentials: React.PropTypes.func,
  };

  constructor(props) {
    super(props);
  }

  componentWillMount() {
    this.props.AccountClearCredentials();
    this.props.LoginWsLogout();
    this.context.router.push('/login');
  }

  render() {
    return <div/>;
  }
}
