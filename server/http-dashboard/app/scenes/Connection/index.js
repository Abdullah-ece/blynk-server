import * as AccountAPI from "data/Account/actions";
import React from 'react';
import * as API from "scenes/Login/data/actions";
import {displayError} from "services/ErrorHandling";

import {
  ConnectionLoading
} from './components';

import {
  ConnectFailed,
  ConnectSuccess,
} from 'data/Connection/actions';

import {message} from 'antd';

import {
  CONNECTION_STATES_VALUES
} from 'data/Connection/reducers';

import {
  blynkWsConnect,
  blynkWsLogin
} from 'store/blynk-websocket-middleware/actions';

import {connect} from 'react-redux';

import PropTypes from 'prop-types';

// import Login from 'scenes/Login';
// import LoginLayout from 'components/LoginLayout';

import {LoginWsLogout, LoginWsSuccess} from 'data/Login/actions';

import {bindActionCreators} from 'redux';

// import {encryptUserPassword} from 'services/Crypto';

@connect((state) => ({
  isUserLoggedIn  : state.Login.isWsLoggedIn,
  Account         : state.Account,
  connectionStatus: state.Connection.connection,
  credentials     : state.Account.credentials || {},
}), (dispatch) => ({
  AccountFetch           : bindActionCreators(AccountAPI.Account, dispatch),
  LoginWsSuccess         : bindActionCreators(LoginWsSuccess, dispatch),
  Login                  : bindActionCreators(API.Login, dispatch),
  blynkWsConnect         : bindActionCreators(blynkWsConnect, dispatch),
  blynkWsLogin           : bindActionCreators(blynkWsLogin, dispatch),
  connectSuccess         : bindActionCreators(ConnectSuccess, dispatch),
  connectFailed          : bindActionCreators(ConnectFailed, dispatch),
  LoginWsLogout          : bindActionCreators(LoginWsLogout, dispatch),
  AccountClearCredentials: bindActionCreators(AccountAPI.AccountClearCredentials, dispatch),
}))
class Connection extends React.Component {

  static contextTypes = {
    router: PropTypes.object,
  };

  static propTypes = {
    connectionStatus       : PropTypes.number,
    isUserLoggedIn         : PropTypes.bool,
    router                 : PropTypes.object,
    Account                : PropTypes.object,
    children               : PropTypes.object,
    credentials            : PropTypes.shape({
      username: PropTypes.string,
      password: PropTypes.string,
    }),
    blynkWsConnect         : PropTypes.func,
    blynkWsLogin           : PropTypes.func,
    connectSuccess         : PropTypes.func,
    connectFailed          : PropTypes.func,
    Login                  : PropTypes.func,
    LoginWsSuccess         : PropTypes.func,
    AccountFetch           : PropTypes.func,
    LoginWsLogout          : PropTypes.func,
    AccountClearCredentials: PropTypes.func,
  };

  componentWillMount() {

    const LOGIN = this.props.credentials.username;
    const PASSWORD = this.props.credentials.password;

    this.props.blynkWsConnect().then(() => {

      if (!LOGIN || !PASSWORD) {
        this.props.connectSuccess();
        return null;
      }

      this.props.blynkWsLogin({
        username: LOGIN,
        hash    : PASSWORD,
      }).then(() => {
        this.props.AccountFetch().then(() => {
          this.props.LoginWsSuccess();
          this.context.router.push('/devices');
          this.props.connectSuccess();
        });
      }).catch((err) => {
        displayError(err, message.error);
        this.props.connectSuccess();
        this.props.AccountClearCredentials();
        this.props.LoginWsLogout();
        this.context.router.push('/login');
      });

    }).catch(() => {
      this.props.connectFailed();
    });
  }

  render() {

    let placeholder = '';

    if(this.props.connectionStatus === CONNECTION_STATES_VALUES.NOT_CONNECTED)
      placeholder = 'Connecting';

    if(this.props.connectionStatus === CONNECTION_STATES_VALUES.SUCCESS)
      placeholder = 'Connected. Logging in';

    if(this.props.connectionStatus === CONNECTION_STATES_VALUES.FAILED)
      placeholder = 'Cannot connect to server';

    if(this.props.connectionStatus === CONNECTION_STATES_VALUES.INTERRUPTED)
      placeholder = 'Connection interrupted. Will try to reconnect in few seconds';


    if(this.props.connectionStatus !== CONNECTION_STATES_VALUES.SUCCESS) {
      return (
        <ConnectionLoading placeholder={placeholder}/>
      );
    }

    return this.props.children;
  }

}

export default Connection;
