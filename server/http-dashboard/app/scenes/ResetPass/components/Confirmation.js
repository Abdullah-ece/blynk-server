import React from 'react';

export default class Confirmation extends React.Component {

  static propTypes = {
    router: React.PropTypes.object
  };

  loginClickHandler() {
    this.props.router.push('/login');
  }

  render() {
    return (<div className="confirm-container">
      <div className="form-header">Password change</div>
      <div className="confirm-message">
        <div>Password was changed successfully.</div>

        <a className="back-to-login" onClick={this.loginClickHandler.bind(this)}>Back to login</a>
      </div>
    </div>);
  }
}
