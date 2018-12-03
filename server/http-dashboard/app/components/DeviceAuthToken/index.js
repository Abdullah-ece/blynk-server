import React from 'react';
import { Button, message } from 'antd';
import classnames from 'classnames';
import CopyToClipboard from 'react-copy-to-clipboard';
import './styles.less';
import DeviceAuthTokenModal from "./modal";
import { Modal } from 'components';
import connect from "react-redux/es/connect/connect";
import { bindActionCreators } from "redux";
import { SetAuthToken } from 'data/Devices/api';

@connect((state) => ({
  account: state.Account,
  orgId: state.Account.selectedOrgId,
}), (dispatch) => ({
  setAuthToken: bindActionCreators(SetAuthToken, dispatch)
}))
class DeviceAuthToken extends React.Component {

  static propTypes = {
    authToken: React.PropTypes.string,
    onCopy: React.PropTypes.func,
    deviceId: React.PropTypes.number,
  };

  constructor(props) {
    super(props);

    this.handleCancelClick = this.handleCancelClick.bind(this);
    this.handleEdit = this.handleEdit.bind(this);
    this.handleOkClick = this.handleOkClick.bind(this);
    this.onChange = this.onChange.bind(this);
    this.getEditableComponent = this.getEditableComponent.bind(this);

    if (!props.authToken) {
      throw new Error('Missing authToken parameter for DeviceAuthToken');
    }
  }

  state = {
    isHovered: false,
    editVisible: false,
    currentValue: this.props.authToken,
    error: '',
  };

  componentWillReceiveProps(props) {
    this.setState({
      currentValue: props.authToken
    });
  }

  handleEdit() {
    this.setState({
      editVisible: true
    });
  }

  startLoading() {
    this.setState({
      loading: true
    });
  }

  stopLoading() {
    this.setState({
      loading: false
    });
  }

  onCancel() {
    this.setState({
      currentValue: this.props.authToken
    });
  }

  closeModal() {
    this.setState({
      editVisible: false
    });
  }

  handleOkClick() {
    this.startLoading();
    if (this.state.currentValue.length != 32) {
      this.setState({
        error: 'Auth Token length must be 32 char long!',
        loading: false
      });
    } else {
      this.setState({
        error: '',
        loading: false
      });
      
      this.props.setAuthToken(this.props.deviceId, this.props.orgId, this.state.currentValue);

      this.closeModal();
    }
  }

  handleCancelClick() {
    this.setState({
      editVisible: false
    });

    if (this.onCancel) {
      this.onCancel();
    }
  }

  getDeviceAuthToken() {
    const lastFourDigits = this.props.authToken.substr(-4);

    return `•••• - •••• - •••• - ${lastFourDigits}`;
  }

  onChange({ target }) {
    this.setState({
      currentValue: target.value
    });
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
    if (this.hideMessageSuccess) return null;
    this.hideMessageSuccess = message.success('Auth token copied to clipboard', 0);

    setTimeout(() => {
      this.hideMessageSuccess = this.hideMessageSuccess();
    }, 2500);

    if (typeof this.props.onCopy === 'function') {
      this.props.onCopy();
    }
  }

  getEditableComponent() {
    const input = {
      value: this.state.currentValue,
      onChange: (value) => this.onChange(value)
    };

    return (
      <div>
        <DeviceAuthTokenModal placeholder="Value" name="value"
                              input={input} error={this.state.error}/>
      </div>
    );
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
        {this.getDeviceAuthToken()}
        <CopyToClipboard text={this.props.authToken}
                         onCopy={this.handleCopyClick.bind(this)}>
          <Button icon="copy" size="small" className={className}/>
        </CopyToClipboard>
        <Button icon="edit" size="small" className={className}
                onClick={this.handleEdit}/>
        <Modal visible={this.state.editVisible}
               wrapClassName={`device-metadata-modal ${this.props.modalWrapClassName || ''}`}
               closable={false}
               title={'Auth Token'}
               onCancel={this.handleCancelClick}
               footer={[
                 <Button key="cancel" type="primary" size="default"
                         onClick={this.handleCancelClick}>Cancel</Button>,
                 <Button key="save" size="default"
                         disabled={!!this.props.errors}
                         loading={this.state.loading}
                         onClick={this.handleOkClick}>
                   Save
                 </Button>,
               ]}
        >
          {this.getEditableComponent()}
        </Modal>
      </div>
    );
  }

}

export default DeviceAuthToken;
