import React from 'react';
import {reduxForm} from 'redux-form';
import {MetadataField} from 'components/Form';
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
            <MetadataField name="firstName" placeholder="First name"/>
          </Col>
          <Col span={12}>
            <MetadataField name="streetAddress" placeholder="Street Address"/>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <MetadataField name="lastName" placeholder="Last name"/>
          </Col>
          <Col span={12}>
            <MetadataField name="city" placeholder="City"/>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <MetadataField name="email" placeholder="E-mail"/>
          </Col>
          <Col span={12}>
            <MetadataField name="state" placeholder="State"/>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <MetadataField name="phone" placeholder="Phone"/>
          </Col>
          <Col span={12}>
            <MetadataField name="zip" placeholder="ZIP"/>
          </Col>
        </Row>
      </div>
    );
  }

}

export default ContactModal;
