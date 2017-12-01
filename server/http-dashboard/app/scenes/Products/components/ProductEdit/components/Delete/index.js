import React from 'react';
import './styles.less';
import {Modal, Icon} from 'antd';
import DeleteForm from './components/DeleteForm';

class Delete extends React.Component {

  static propTypes = {
    visible: React.PropTypes.bool,

    handleSubmit: React.PropTypes.func,
    onCancel: React.PropTypes.func,

    deviceCount: React.PropTypes.number,

    productName: React.PropTypes.string
  };

  render() {
    return (
      <Modal width={380} onCancel={this.props.onCancel} visible={this.props.visible} footer={null} closable={false}
             wrapClassName="product-delete-confirmation-modal delete-confirmation-modal vertical-center-modal">
        <div className="delete-confirmation-modal-wrapper">
          <div className="delete-confirmation-modal-title">
            <Icon type="exclamation-circle"/> Are you sure?
          </div>
          <div className="delete-confirmation-modal-content">
            <div className="delete-confirmation-modal-content-notice">
              This will remove Info, Metadata, Data Streams<br/>
              from <span>{this.props.deviceCount}</span> devices. It cannot be undone.
            </div>
            <div className="delete-confirmation-modal-content-sub-notice">
              Sensor data and devices logs will not be deleted
            </div>
            <DeleteForm onCancel={this.props.onCancel} onSubmit={this.props.handleSubmit.bind(this)}
                        productName={this.props.productName}/>
          </div>
        </div>
      </Modal>
    );
  }
}

export default Delete;
