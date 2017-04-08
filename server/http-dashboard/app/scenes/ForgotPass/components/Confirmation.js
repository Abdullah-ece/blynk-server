import React from 'react';

export default class Confirmation extends React.Component {

  render() {
    return <div className="confirm-container">
      <div className="form-header">Forgot password?</div>
      <div className="confirm-message">
        Please check your email for further instructions.
      </div>
    </div>
  }
}
