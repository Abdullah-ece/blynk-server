import React from 'react';

import LoginForm from './components/LoginForm';

import { bindActionCreators } from 'redux';

import { SubmissionError } from 'redux-form';

import { LoginWsSuccess } from 'data/Login/actions';

import { connect } from 'react-redux';

import * as AccountAPI from 'data/Account/actions';

import {
  blynkWsLogin
} from 'store/blynk-websocket-middleware/actions';


import { encryptUserPassword } from 'services/Crypto';
import { OrganizationSwitch } from "../../data/Organization/actions";

@connect((state) => {
  return {
    orgId: state.Account.selectedOrgId,
  };
}, (dispatch) => {
  return {
    LoginWsSuccess: bindActionCreators(LoginWsSuccess, dispatch),
    AccountSaveCredentials: bindActionCreators(AccountAPI.AccountSaveCredentials, dispatch),
    AccountFetch: bindActionCreators(AccountAPI.Account, dispatch),
    blynkWsLogin: bindActionCreators(blynkWsLogin, dispatch),
    organizationSwitch: bindActionCreators(OrganizationSwitch, dispatch),
  };
})
export default class Login extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    blynkWsLogin: React.PropTypes.func,
    LoginWsSuccess: React.PropTypes.func,
    UnmarkAsRecentRegistered: React.PropTypes.func,
    AccountFetch: React.PropTypes.func,
    AccountSaveCredentials: React.PropTypes.func,
    organizationSwitch: React.PropTypes.func,
    isRecentlyRegistered: React.PropTypes.bool,
    orgId: React.PropTypes.oneOfType([React.PropTypes.string, React.PropTypes.number]),
  };

  constructor() {
    super();

    this.state = {
      loading: false
    };
  }

  handleSubmit(values) {

    const password = encryptUserPassword(values.email, values.password);

    this.setState({
      loading: true
    });


    return this.props.blynkWsLogin({
      username: values.email,
      hash: password
    }).catch(() => {
      this.setState({
        loading: false
      });
      throw new SubmissionError({ _error: 'Incorrect email or password. Please try again.' });
    }).then(() => {
      this.props.AccountSaveCredentials({
        username: values.email,
        password: password,
      });
      //todo this is not required since api send back user data on successful login
      this.props.AccountFetch().then(() => {
        this.props.LoginWsSuccess();
        if (this.props.orgId) {
          this.props.organizationSwitch({
            orgId: this.props.orgId
          });
        }
        this.context.router.push('/devices');
      });
    });
  }

  render() {

    return (<LoginForm onSubmit={this.handleSubmit.bind(this)}
                       router={this.context.router}
                       loading={this.state.loading}/>);
  }

}
