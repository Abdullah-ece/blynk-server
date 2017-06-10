import React from 'react';
import {reduxForm} from 'redux-form';
import {Item, Input} from 'components/UI';
import {Row, Col} from 'antd';

import './styles.less';

@reduxForm({
  form: 'deviceMetadataEdit'
})
class ContactModal extends React.Component {

  render() {
    return (
      <div className="device-metadata--contact-modal">
        <Row>
          <Col span={12}>
            <Item label="First Name" offset="normal">
              <Input name="firstName" placeholder="First name"/>
            </Item>
          </Col>
          <Col span={12}>
            <Item label="Street Address" offset="normal">
              <Input name="streetAddress" placeholder="Street Address"/>
            </Item>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <Item label="Last name" offset="normal">
              <Input name="lastName" placeholder="Last name"/>
            </Item>
          </Col>
          <Col span={12}>
            <Item label="City" offset="normal">
              <Input name="city" placeholder="City"/>
            </Item>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <Item label="E-mail" offset="normal">
              <Input name="email" placeholder="E-mail"/>
            </Item>
          </Col>
          <Col span={12}>
            <Item label="State" offset="normal">
              <Input name="state" placeholder="State"/>
            </Item>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <Item label="Phone" offset="normal">
              <Input name="phone" placeholder="Phone"/>
            </Item>
          </Col>
          <Col span={12}>
            <Item label="ZIP" offset="normal">
              <Input name="zip" placeholder="ZIP"/>
            </Item>
          </Col>
        </Row>
      </div>
    );
  }

}

export default ContactModal;
