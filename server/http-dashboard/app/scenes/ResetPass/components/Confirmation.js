import React from 'react';
import {Link} from 'react-router';
import {Button} from "antd";

export default class Confirmation extends React.Component {

  static propTypes = {
    router: React.PropTypes.object
  };

  render() {
    return (<div className="confirm-container">
      <div className="form-header">Password change</div>
      <div>
        <div>Password was changed successfully.</div>

        <Link className="back-to-login" to="/login">
          <Button type="primary" className="reset-pass-confirmation-back-to-login-btn">Continue</Button>
        </Link>
      </div>
    </div>);
  }
}
