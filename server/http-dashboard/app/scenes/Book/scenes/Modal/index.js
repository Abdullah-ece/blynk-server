import React from 'react';
import Highlight from 'react-highlight';
import {Modal} from 'components';
import {Button, Input, Row, Col} from 'antd';

class ModalBook extends React.Component {

  state = {
    modalVisible: false,
    confirmLoading: false
  };

  showModal() {
    this.setState({
      modalVisible: true
    });
  }

  handleOK() {
    this.setState({
      confirmLoading: true
    });
    setTimeout(() => {
      this.setState({
        modalVisible: false,
        confirmLoading: false
      });
    }, 2000);
  }

  handleCancel() {
    this.setState({
      modalVisible: false
    });
  }

  render() {
    return (
      <div>

        <i><strong>This is <a target="_blank" href="https://ant.design/components/modal/">Ant native Modal</a> but just
          styled for us</strong></i>

        <h4>Example</h4>

        <Button onClick={this.showModal.bind(this)}>
          Open Modal
        </Button>

        <Modal title="Farm Owner"
               visible={this.state.modalVisible}
               confirmLoading={this.state.confirmLoading}
               onOk={this.handleOK.bind(this)}
               okText="Save"
               cancelText="Cancel"
               onCancel={this.handleCancel.bind(this)}>
          <Row>
            <Col span={12} style={{paddingRight: 16}}>
              <Input placeholder="First Name"/>
            </Col>
            <Col span={12} style={{paddingLeft: 16}}>
              <Input placeholder="Company Name"/>
            </Col>
          </Row>
          <Row style={{marginTop: 8}}>
            <Col span={12} style={{paddingRight: 16}}>
              <Input placeholder="Last Name"/>
            </Col>
            <Col span={12} style={{paddingLeft: 16}}>
              <Input placeholder="Company Address"/>
            </Col>
          </Row>
        </Modal>

        <h4>Code</h4>

        <Highlight className="html">
          {`
<Modal title="Farm Owner"
       visible={this.state.modalVisible}
       confirmLoading={this.state.confirmLoading}
       onOk={this.handleOK.bind(this)}
       okText="Save"
       cancelText="Cancel"
       onCancel={this.handleCancel.bind(this)}>
  <Row>
    <Col span={12} style={{paddingRight: 16}}>
      <Input placeholder="First Name" />
    </Col>
    <Col span={12} style={{paddingLeft: 16}}>
      <Input placeholder="Company Name" />
    </Col>
  </Row>
  <Row style={{marginTop: 8}}>
    <Col span={12} style={{paddingRight: 16}}>
      <Input placeholder="Last Name" />
    </Col>
    <Col span={12} style={{paddingLeft: 16}}>
      <Input placeholder="Company Address" />
    </Col>
  </Row>
</Modal>`}
        </Highlight>

      </div>
    );
  }

}

export default ModalBook;
