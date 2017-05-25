import React from 'react';
import {Button, Form} from 'antd';
import {reduxForm} from 'redux-form';
import {Field} from 'components/Form';
import Validation from 'services/Validation';

@reduxForm({
  form: 'product-delete-confirmation',
  validate: function (values, props) {
    const errors = {};

    if (values.productName !== props.productName) {
      errors.productName = 'Product name does not match';
    }

    return errors;
  }
})
class DeleteForm extends React.Component {

  static propTypes = {
    invalid: React.PropTypes.bool,
    submitting: React.PropTypes.bool,

    onOk: React.PropTypes.func,
    onCancel: React.PropTypes.func,
    handleSubmit: React.PropTypes.func,

    productName: React.PropTypes.string
  };

  render() {
    return (
      <Form onSubmit={this.props.handleSubmit}>
        <div className="delete-confirmation-modal-input">
          <Field name="productName" displayError={false} placeholder={`Type in ${this.props.productName}`} validate={[
            Validation.Rules.required
          ]}/>
        </div>
        <div className="delete-confirmation-modal-buttons">
          <Button type="default" className="delete-confirmation-modal-buttons-cancel" onClick={this.props.onCancel}>Cancel</Button>
          <Button type="danger"
                  className="delete-confirmation-modal-buttons-confirm"
                  htmlType="submit"
                  loading={this.props.submitting}
                  disabled={this.props.invalid || this.props.submitting}>
            Yes, Delete Product
          </Button>
        </div>
      </Form>
    );
  }
}

export default DeleteForm;
