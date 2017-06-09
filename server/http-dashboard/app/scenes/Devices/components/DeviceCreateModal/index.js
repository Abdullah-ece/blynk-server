import React from 'react';
import {Modal} from 'components';
import {Row, Col, Input, Button} from 'antd';
import {Form} from 'components/UI';
import {MetadataSelect} from 'components/Form';
import {reduxForm} from 'redux-form';

@reduxForm({
  form: "DeviceCreate"
})
class DeviceCreateModal extends React {

  static propTypes = {
    visible: React.PropTypes.bool,
    onClose: React.PropTypes.func,
    errors: React.PropTypes.object
  };

  state = {
    loading: false
  };

  handleCancelClick() {
    this.props.onClose();
  }

  handleOkClick() {

  }

  products = [
    {
      key: '1',
      value: 'Product 1'
    },
    {
      key: '2',
      value: 'Product 2'
    }
  ];

  render() {
    return (
      <Modal title="New Device"
             visible={this.props.visible}
             confirmLoading={this.state.loading}
             onCancel={this.handleCancelClick.bind(this)}
             footer={[
               <Button key="cancel" type="primary" size="default"
                       onClick={this.handleCancelClick.bind(this)}>Cancel</Button>,
               <Button key="save" size="default" disabled={!!this.props.errors} loading={this.state.loading}
                       onClick={this.handleOkClick.bind(this)}>
                 Create
               </Button>,
             ]}>
        <Row>
          <Col span={24}>
            <Form.Item label="Device Name" offset="large">
              <Input name="name" placeholder="New Device"/>
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <Form.Item label="Product Template" offset="large">
              <MetadataSelect values={this.products} placeholder="Choose product"/>
            </Form.Item>
          </Col>
        </Row>
      </Modal>
    );
  }

}

export default DeviceCreateModal;
