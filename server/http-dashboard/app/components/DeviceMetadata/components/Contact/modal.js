import React from 'react';
import {reduxForm} from 'redux-form';
import {Item, Input} from 'components/UI';
import {Row, Col} from 'antd';
import Validation from 'services/Validation';

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
              <Input name="firstName" placeholder="First name" validate={[Validation.Rules.required]}/>
            </Item>
          </Col>
          <Col span={12}>
            <Item label="Street Address" offset="normal">
              <Input name="streetAddress" placeholder="Street Address" validate={[Validation.Rules.required]}/>
            </Item>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <Item label="Last name" offset="normal">
              <Input name="lastName" placeholder="Last name" validate={[Validation.Rules.required]}/>
            </Item>
          </Col>
          <Col span={12}>
            <Item label="City" offset="normal">
              <Input name="city" placeholder="City" validate={[Validation.Rules.required]}/>
            </Item>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <Item label="E-mail" offset="normal">
              <Input name="email" placeholder="E-mail" validate={[Validation.Rules.required, Validation.Rules.email]}
                     validateOnBlur={true}/>
            </Item>
          </Col>
          <Col span={12}>
            <Item label="State" offset="normal">
              <Input name="state" placeholder="State" validate={[Validation.Rules.required]}/>
            </Item>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <Item label="Phone" offset="normal">
              <Input name="phone" placeholder="Phone" validate={[Validation.Rules.required]}/>
            </Item>
          </Col>
          <Col span={12}>
            <Item label="ZIP" offset="normal">
              <Input name="zip" placeholder="ZIP" validate={[Validation.Rules.required]}/>
            </Item>
          </Col>
        </Row>
      </div>
    );
  }

}

export default ContactModal;
