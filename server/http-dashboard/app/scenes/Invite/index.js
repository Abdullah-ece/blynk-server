import React from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import './styles.less';
import {SubmissionError} from 'redux-form';
import InviteForm from './components/InviteForm';
import {encryptUserPassword} from 'services/Crypto';
import {Invite as SendInvite} from './data/actions';
import {Account as AccountFetch} from 'data/Account/actions';

@connect(() => ({}), (dispatch) => ({
  SendInvite: bindActionCreators(SendInvite, dispatch),
  AccountFetch: bindActionCreators(AccountFetch, dispatch)
}))
class Invite extends React.Component {

  static propTypes = {
    location: React.PropTypes.object,
    SendInvite: React.PropTypes.func,
    AccountFetch: React.PropTypes.func
  };

  static contextTypes = {
    router: React.PropTypes.object
  };

  handleSubmit(values) {

    const password = encryptUserPassword(this.props.location.query.email, values.password);

    return this.props.SendInvite({
      token: this.props.location.query.token,
      password: password
    }).then(() => {
      this.props.AccountFetch().then(() => {
        this.context.router.push('/devices');
      });
    }).catch((err) => {
      throw new SubmissionError({
        _error: err.error.response.message
      });
    });
  }

  render() {
    return (
      <InviteForm onSubmit={this.handleSubmit.bind(this)}/>
    );
  }
}

export default Invite;
