import React from 'react';

import LoginForm from './components/LoginForm';

import {bindActionCreators} from 'redux';

import {SubmissionError} from 'redux-form';

import {connect} from 'react-redux';

import * as API from './data/actions';
import * as AccountAPI from 'data/Account/actions';

import {encryptUserPassword} from 'services/Crypto';

@connect(() => {
  return {};
}, (dispatch) => {
  return {
    AccountFetch: bindActionCreators(AccountAPI.Account, dispatch),
    Login: bindActionCreators(API.Login, dispatch)
  };
})
export default class Login extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    Login: React.PropTypes.func,
    UnmarkAsRecentRegistered: React.PropTypes.func,
    AccountFetch: React.PropTypes.func,
    isRecentlyRegistered: React.PropTypes.bool
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
    return this.props.Login({
      email: values.email,
      password: password
    }).catch(() => {
      this.setState({
        loading: false
      });
      throw new SubmissionError({_error: 'Wrong email or password'});
    }).then(() => {
      this.props.AccountFetch().then(() => {
        this.context.router.push('/account');
      });
    });
  }

  render() {

    return (
      <LoginForm onSubmit={this.handleSubmit.bind(this)} loading={this.state.loading}/>
    );
  }

}
