import React from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import './styles.less';
import {message} from 'antd';
import InviteForm from './components/InviteForm';
import {encryptUserPassword} from 'services/Crypto';
import {Invite as SendInvite} from './data/actions';
import * as AccountAPI from 'data/Account/actions';
import {LoginWsSuccess} from 'data/Login/actions';
import {
  blynkWsLoginViaInvite
} from 'store/blynk-websocket-middleware/actions';
import {displayError} from "../../services/ErrorHandling";


@connect(() => ({}), (dispatch) => ({
  LoginWsSuccess: bindActionCreators(LoginWsSuccess, dispatch),
  blynkWsLogin: bindActionCreators(blynkWsLoginViaInvite, dispatch),
  SendInvite: bindActionCreators(SendInvite, dispatch),
  AccountFetch: bindActionCreators(AccountAPI.Account, dispatch),
  AccountSaveCredentials: bindActionCreators(AccountAPI.AccountSaveCredentials, dispatch),
}))
class Invite extends React.Component {

  static propTypes = {
    location: React.PropTypes.object,
    SendInvite: React.PropTypes.func,
    blynkWsLogin: React.PropTypes.func,
    AccountSaveCredentials: React.PropTypes.func,
    LoginWsSuccess: React.PropTypes.func,
    AccountFetch: React.PropTypes.func
  };

  static contextTypes = {
    router: React.PropTypes.object
  };

  handleSubmit(values) {

    const password = encryptUserPassword(this.props.location.query.email, values.password);

    return this.props.blynkWsLogin({
      username: this.props.location.query.token,
      hash: password
    }).then(() => {
      this.props.AccountSaveCredentials({
        username: this.props.location.query.email,
        password: password,
      });
      //todo this is not required since api send back user data on successful login
      this.props.AccountFetch().then(() => {
        this.props.LoginWsSuccess();
        this.context.router.push('/devices');
      });
    }).catch((err) => {
      displayError(err, message.error);
    });

  }

  render() {
    return (
      <InviteForm onSubmit={this.handleSubmit.bind(this)}/>
    );
  }
}

export default Invite;
