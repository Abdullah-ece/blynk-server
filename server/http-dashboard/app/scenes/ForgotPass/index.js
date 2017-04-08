import React from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {SubmissionError} from 'redux-form';

import * as API from './data/actions';
import ForgotPassForm from "./components/ForgotPassForm";
import Confirmation from "./components/Confirmation";
import './styles.scss';

@connect(() => {
  return {};
}, (dispatch) => {
  return {
    ForgotPass: bindActionCreators(API.ForgotPass, dispatch)
  };
})
export default class ForgotPass extends React.Component {

  static contextTypes = {
    router: React.PropTypes.object
  };

  static propTypes = {
    ForgotPass: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.state = {
      loading: false,
      success: false
    };
  }

  handleSubmit(values) {
    this.setState({ loading: true });
    return this.props.ForgotPass(values).catch(() => {
      this.setState({
        loading: false
      });
      throw new SubmissionError({_error: "Account doesn't exist."});
    }).then(() => {
      this.setState({
        loading: false,
        success: true
      });
    });
  }

  render() {
    const success = this.state.success;

    return (<div className="forgot-pass">
      {success ? <Confirmation/> : <ForgotPassForm onSubmit={this.handleSubmit.bind(this)}/>}
    </div>);
  }
}
