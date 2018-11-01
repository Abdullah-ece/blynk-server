import React from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {SubmissionError} from 'redux-form';

import {encryptUserPassword} from 'services/Crypto';
import {ResetPassword} from "data/ResetPass/actions";
import ResetPassForm from "./components/ResetPassForm";
import Confirmation from "./components/Confirmation";
import {displayError} from "services/ErrorHandling";
import './styles.less';

@connect(() => {
  return {};
}, (dispatch) => {
  return {
    ResetPass: bindActionCreators(ResetPassword, dispatch)
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
    const hash = encryptUserPassword(email, values.password);

    this.setState({ loading: true });
    return this.props.ResetPass({token, hash}).catch((err) => {
      this.setState({
        loading: false
      });
      throw new SubmissionError({_error: displayError(err)});
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
