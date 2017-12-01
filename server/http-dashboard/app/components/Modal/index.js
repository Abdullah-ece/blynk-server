import React from 'react';
import {Modal as AntModal} from 'antd';
import './styles.less';

class Modal extends React.Component {

  static propTypes = {
    wrapClassName: React.PropTypes.string
  };

  render() {

    return (
      <AntModal {...this.props} wrapClassName={`modal-window ${this.props.wrapClassName || ''} vertical-center-modal`}/>
    );
  }

}

Modal.info = AntModal.info;
Modal.error = AntModal.error;
Modal.success = AntModal.success;
Modal.warning = AntModal.warning;
Modal.confirm = AntModal.confirm;

export default Modal;
