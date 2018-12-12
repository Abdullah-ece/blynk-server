import React from 'react';
import { Link } from 'react-router';

import './styles.less';

export default class LoginLayout extends React.Component {

  static propTypes = {
    children: React.PropTypes.object
  };

  render() {
    return (<div className="login-scene">
      <div className="login-logo-wrapper">
        <Link to="/login">
          <div className="login-logo"/>
        </Link>
      </div>
      <div className="login-form">
        { this.props.children }
      </div>
      <div className="login-bottom-label">{process.env.BLYNK_POWERED_BY && JSON.parse(process.env.BLYNK_POWERED_BY) && `⚡ powered by Blynk %(built_date)s`}</div>
    </div>);
  }
}
