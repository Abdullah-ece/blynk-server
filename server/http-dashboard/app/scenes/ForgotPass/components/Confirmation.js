import React from 'react';
import {Button} from 'antd';
import PropTypes from 'prop-types';

export default class Confirmation extends React.Component {

  static contextTypes = {
    router: PropTypes.object
  };

  constructor(props) {
    super(props);

    this.backButtonHandler = this.backButtonHandler.bind(this);
  }

  backButtonHandler() {
    this.context.router.push('/login');
  }

  render() {
    return (
      <div className="confirm-container">
        <div className="form-header">Password reset</div>
        <div className="password-restoration-info-message">
          Check your inbox for instructions on how to reset your password
        </div>
        <Button type="primary"
                size="default"
                onClick={this.backButtonHandler}>
                Back to Login
        </Button>
      </div>
    );
  }
}
