import React from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {SubmissionError} from 'redux-form';

import {encryptUserPassword} from 'services/Crypto';
import * as API from './data/actions';
import ResetPassForm from "./components/ResetPassForm";
import Confirmation from "./components/Confirmation";
import './styles.less';

@connect(() => {
  return {};
}, (dispatch) => {
  return {
    ResetPass: bindActionCreators(API.ResetPass, dispatch)
  };
})
export default class ResetPass extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    ResetPass: React.PropTypes.func,
    location: React.PropTypes.object,
  };

  constructor(props) {
    super(props);

    this.state = {
      loading: false,
      success: false
    };
  }

  componentWillMount() {
    if (!this.props.location.query || !this.props.location.query.token || !this.props.location.query.email) {
      this.context.router.push('/devices');
    }
  }

  componentWillUnmount() {
    if (this.redirectTimeout) {
      clearTimeout(this.redirectTimeout);
    }
  }

  handleSubmit(values) {
    const token = this.props.location.query.token;
    const email = this.props.location.query.email;
    const password = encryptUserPassword(email, values.password);

    this.setState({ loading: true });
    return this.props.ResetPass({token, password}).catch(() => {
      this.setState({
        loading: false
      });
      throw new SubmissionError({_error: "Password change failed."});
    }).then(() => {
      this.setState({
        loading: false,
        success: true
      });

      this.redirectTimeout = setTimeout(() => this.context.router.push('/login'), 5000);
    });
  }

  render() {
    const success = this.state.success;

    return (<div className="reset-pass">
      {success ? <Confirmation router={this.context.router}/> : <ResetPassForm onSubmit={this.handleSubmit.bind(this)}/>}
    </div>);
  }
}
