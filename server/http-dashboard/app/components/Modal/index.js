import React from 'react';
import {Modal as AntModal} from 'antd';
import './styles.less';

class Modal extends React.Component {

  render() {

    return (
      <AntModal {...this.props} wrapClassName={`modal-window ${this.props.wrapClassName || ''}`}/>
    );
  }

}

export default Modal;
