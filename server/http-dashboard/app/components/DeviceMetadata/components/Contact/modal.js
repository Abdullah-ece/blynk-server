import React from 'react';
import {Item, Input} from 'components/UI';
import {Row, Col} from 'antd';
import Validation from 'services/Validation';

import './styles.less';

class ContactModal extends React.Component {

  static propTypes = {
    data: React.PropTypes.object
  };

  render() {

    const fields = [
      {
        name: 'firstName',
        label: 'First Name',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.isFirstNameEnabled
      },
      {
        name: 'lastName',
        label: 'Last Name',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.isLastNameEnabled
      },
      {
        name: 'streetAddress',
        label: 'Street Address',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.isStreetAddressEnabled
      },
      {
        name: 'city',
        label: 'City',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.isCityEnabled
      },
      {
        name: 'email',
        label: 'E-mail',
        validate: [Validation.Rules.required, Validation.Rules.email],
        isEnabled: this.props.data.isEmailEnabled,
        validateOnBlur: true
      },
      {
        name: 'state',
        label: 'State',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.isStateEnabled
      },
      {
        name: 'phone',
        label: 'Phone',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.isPhoneEnabled
      },
      {
        name: 'zip',
        label: 'ZIP',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.isZipEnabled
      }
    ].filter((field) => {
      return !!field.isEnabled;
    });

    const form = [];

    // if we have only 1 field we should
    // make if to full modal width
    const span = fields.length === 1 ? 24 : 12;

    const rows = Math.ceil(fields.length / 2);

    for (let i = 0; i < rows; i++) {
      const getFields = (i) => {
        return [fields[i * 2] || null, fields[i * 2 + 1] || null];
      };

      form.push(
        <Row key={i}>
          { getFields(i).map((item, key) => {
            if (item)
              return (
                <Col span={span} key={key}>
                  <Item label={item.label} offset={rows - i === 1 ? `none` : `normal`}>
                    <Input name={item.name} placeholder={item.label} validate={item.validate}
                           validateOnBlur={item.validateOnBlur || false}/>
                  </Item>
                </Col>
              );
            return null;
          })}
        </Row>
      );

    }

    return (
      <div className="device-metadata--contact-modal">
        { form }
      </div>
    );
  }

}

export default ContactModal;
