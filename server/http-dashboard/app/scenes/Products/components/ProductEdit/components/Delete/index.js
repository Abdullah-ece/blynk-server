import React from 'react';
import './styles.less';
import {Button, Form, Icon} from 'antd';
import Modal from 'components/Modal';
import {Field} from 'components/Form';
import {reduxForm} from 'redux-form';
import Validation from 'services/Validation';
@reduxForm({
  form: 'product-delete-confirmation',
  validate: function (values) {
    const errors = {};

    if (values.productName !== "DELETE") {
      errors.productName = 'Please type DELETE into the field to confirm action';
    }

    return errors;
  }
})
class Delete extends React.Component {

  static propTypes = {
    visible: React.PropTypes.bool,

    handleSubmit: React.PropTypes.func,
    onCancel: React.PropTypes.func,

    deviceCount: React.PropTypes.number,

    productName: React.PropTypes.string,

    invalid: React.PropTypes.bool,
    submitting: React.PropTypes.bool,
  };

  render() {
    return (
        <Form onSubmit={this.props.handleSubmit}>
        <Modal width={380} onCancel={this.props.onCancel}  className={'delete-modal-form'}  visible={this.props.visible}
               footer={[
                 <Button type="default"
                         key={'Cancel'}
                         className="delete-confirmation-modal-buttons-cancel"
                         onClick={this.props.onCancel}
                 >
                   Cancel
                 </Button>,
                 <Button type="danger"
                         key={"Delete"}
                         className="delete-confirmation-modal-buttons-confirm"
                         onClick={this.props.handleSubmit}
                         loading={this.props.submitting}
                         disabled={this.props.invalid || this.props.submitting}>
                   Delete
                 </Button>
               ]}
               closable={false}
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
              <div className="delete-confirmation-modal-input">
                <Field name="productName" displayError={false} placeholder={`Type in DELETE`} validate={[
                  Validation.Rules.required
                ]}/>
              </div>
            </div>
          </div>
        </Modal>
       </Form>
    );
  }
}

export default Delete;
