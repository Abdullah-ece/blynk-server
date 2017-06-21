import React            from 'react';
import {Input, Item}    from 'components/UI';
import {Modal}          from 'components';
import {Button}         from 'antd';
import './styles.less';

class MarkAsResolvedModal extends React.Component {

  static propTypes = {
    onCancel: React.PropTypes.func,
    event: React.PropTypes.object,
    isModalVisible: React.PropTypes.bool,
  };

  state = {
    loading: false
  };

  handleOk() {

  }

  handleCancel() {
    this.props.onCancel();
  }

  render() {
    return (
      <Modal visible={this.props.isModalVisible}
             confirmLoading={this.state.loading}
             onCancel={this.handleCancel.bind(this)}
             wrapClassName="event-resolve-modal"
             footer={[
               <Button key={'cancel'} onClick={this.handleCancel.bind(this)}>Cancel</Button>,
               <Button key={'submit'} className="positive" type="primary" loading={this.state.loading}
                       onClick={this.handleOk.bind(this)}>Mark as resolved</Button>,
             ]}>
        <div className="event-resolve-modal-header">
          <div className="event-resolve-modal-header-name">
            Flush Error
          </div>
          <div className="event-resolve-modal-header-date">
            Yesterday, 3:06 PM
          </div>
        </div>
        <div className="event-resolve-modal-content">
          <Item label="Add comments(optional)">
            <Input type="textarea" rows="10"/>
          </Item>
        </div>
      </Modal>
    );
  }

}

export default MarkAsResolvedModal;
