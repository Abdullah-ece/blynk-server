import React from 'react';
import {Modal} from 'components';
import {Button} from 'antd';
import Item from '../Item';
import {Metadata} from 'services/Products';

import './styles.less';

class Base extends React.Component {

  static propTypes = {
    form: React.PropTypes.string,
    modalWrapClassName: React.PropTypes.string,
    data: React.PropTypes.object,
    onChange: React.PropTypes.func,
    initialize: React.PropTypes.func,
    resetForm: React.PropTypes.func,
    values: React.PropTypes.object,
    errors: React.PropTypes.object,
    account: React.PropTypes.object,
    isEditDisabled: React.PropTypes.bool,
    availableLocationsList: React.PropTypes.array,
  };

  constructor(props) {
    super(props);
    this.handleCancelClick = this.handleCancelClick.bind(this);
    this.handleEdit        = this.handleEdit.bind(this);
    this.handleOkClick     = this.handleOkClick.bind(this);
  }

  state = {
    editVisible: false
  };

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

  onOk(metafield) {
    this.props.onChange(metafield).then(() => {
      this.stopLoading();
      this.closeModal();
    });
  }

  onCancel() {
    this.props.resetForm(this.props.form);
  }

  closeModal() {
    this.setState({
      editVisible: false
    });
  }

  handleOkClick() {
    if (this.onOk) {
      this.startLoading();
      this.onOk(this.props.values);
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

  render() {

    const field = this.props.data;

    const itemField = this.props.data && this.props.data.type === Metadata.Fields.LOCATION ? this.props.data : {};

    return (
      <Item field={itemField} onEditClick={this.handleEdit} userRole={this.props.account.roleId} fieldRole={field.roleIds} fieldName={field.name} isEditDisabled={this.props.isEditDisabled}>
        { this.getPreviewComponent() }
        <Modal visible={this.state.editVisible}
               wrapClassName={`device-metadata-modal ${this.props.modalWrapClassName || ''}`}
               closable={false}
               title={field.name}
               onCancel={this.handleCancelClick}
               footer={[
                 <Button key="cancel" type="primary" size="default"
                         onClick={this.handleCancelClick}>Cancel</Button>,
                 <Button key="save" size="default" disabled={!!this.props.errors} loading={this.state.loading}
                         onClick={this.handleOkClick}>
                   Save
                 </Button>,
               ]}
        >
          { this.getEditableComponent() }
        </Modal>
      </Item>
    );
  }

}

export default Base;
