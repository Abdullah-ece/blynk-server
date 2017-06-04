import React from 'react';
import {Modal} from 'components';
import {Button} from 'antd';
import Item from '../Item';
import {fromJS} from 'immutable';

class Base extends React.Component {

  static propTypes = {
    form: React.PropTypes.string,
    data: React.PropTypes.object,
    onChange: React.PropTypes.func,
    resetForm: React.PropTypes.func,
    values: React.PropTypes.object,
    errors: React.PropTypes.object
  };

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
    this.props.onChange(metafield);
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
      this.onOk(fromJS(this.props.values));
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

    return (
      <Item onEditClick={this.handleEdit.bind(this)}>
        { this.getPreviewComponent() }
        <Modal visible={this.state.editVisible}
               title={field.get('name')}
               onCancel={this.handleCancelClick.bind(this)}
               footer={[
                 <Button key="cancel" type="primary" size="default"
                         onClick={this.handleCancelClick.bind(this)}>Cancel</Button>,
                 <Button key="save" size="default" disabled={!!this.props.errors} loading={this.state.loading}
                         onClick={this.handleOkClick.bind(this)}>
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
