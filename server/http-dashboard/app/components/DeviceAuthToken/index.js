import React from 'react';
import {Button, message} from 'antd';
import classnames from 'classnames';
import CopyToClipboard from 'react-copy-to-clipboard';
import './styles.less';

class DeviceAuthToken extends React.Component {

  static propTypes = {
    authToken: React.PropTypes.string,
    onCopy: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    if (!props.authToken) {
      throw new Error('Missing authToken parameter for DeviceAuthToken');
    }
  }

  state = {
    isHovered: false
  };

  getDeviceAuthToken() {
    const lastFourDigits = this.props.authToken.substr(-4);

    return `•••• - •••• - •••• - ${lastFourDigits}`;
  }

  handleMouseEnter() {
    this.setState({
      isHovered: true
    });
  }

  handleMouseLeave() {
    this.setState({
      isHovered: false
    });
  }

  handleCopyClick() {
    const messages = document.getElementsByClassName('ant-message-notice');
    if (messages.length === 0) {
      message.success('Auth token copied to clipboard');
      if (typeof this.props.onCopy === 'function') {
        this.props.onCopy();
      }
    }
  }

  render() {

    const className = classnames({
      'device-auth-token-copy': true,
      'show': this.state.isHovered,
      'hide': !this.state.isHovered
    });

    return (
      <div className="device-auth-token"
           onMouseEnter={this.handleMouseEnter.bind(this)}
           onMouseLeave={this.handleMouseLeave.bind(this)}>
        { this.getDeviceAuthToken() }
        <CopyToClipboard text={this.props.authToken} onCopy={this.handleCopyClick.bind(this)}>
          <Button icon="copy" size="small" className={className}/>
        </CopyToClipboard>
      </div>
    );
  }

}

export default DeviceAuthToken;
