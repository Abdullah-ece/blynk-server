import React from 'react';
import {Modal} from 'components';
import {Button} from 'antd';
import Item from '../Item';

class Base extends React.Component {

  static propTypes = {
    data: React.PropTypes.object
  };

  state = {
    editVisible: false
  };

  handleEdit() {
    this.setState({
      editVisible: true
    });
  }

  handleOkClick() {

  }

  getFormName() {
    return `DeviceMetadataEdit${this.props.data.name}`;
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
               title={field.name}
               onCancel={this.handleCancelClick.bind(this)}
               footer={[
                 <Button key="cancel" type="primary" size="default"
                         onClick={this.handleCancelClick.bind(this)}>Cancel</Button>,
                 <Button key="save" size="default" onClick={this.handleOkClick.bind(this)}>
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
