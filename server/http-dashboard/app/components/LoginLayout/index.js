import React from 'react';

import './styles.scss';

export default class LoginLayout extends React.Component {

  static propTypes = {
    children: React.PropTypes.object
  };

  render() {

    return (<div className="login-scene">
      <div className="login-logo-wrapper">
        <div className="login-logo"/>
      </div>
      <div className="login-form">
        { this.props.children }
      </div>
      <div className="login-bottom-label">⚡ powered by Blynk</div>
    </div>);
  }
}
