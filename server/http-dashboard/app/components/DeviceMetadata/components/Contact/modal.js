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

  static propTypes = {
    data: React.PropTypes.object
  };

  render() {

    const fields = [
      {
        name: 'firstName',
        label: 'First Name',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.get('isFirstNameEnabled')
      },
      {
        name: 'lastName',
        label: 'Last Name',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.get('isLastNameEnabled')
      },
      {
        name: 'streetAddress',
        label: 'Street Address',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.get('isStreetAddressEnabled')
      },
      {
        name: 'city',
        label: 'City',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.get('isCityEnabled')
      },
      {
        name: 'email',
        label: 'E-mail',
        validate: [Validation.Rules.required, Validation.Rules.email],
        isEnabled: this.props.data.get('isEmailEnabled'),
        validateOnBlur: true
      },
      {
        name: 'state',
        label: 'State',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.get('isStateEnabled')
      },
      {
        name: 'phone',
        label: 'Phone',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.get('isPhoneEnabled')
      },
      {
        name: 'zip',
        label: 'ZIP',
        validate: [Validation.Rules.required],
        isEnabled: this.props.data.get('isZipEnabled')
      }
    ].filter((field) => {
      return !!field.isEnabled;
    });

    const form = [];

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
                <Col span={12} key={key}>
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
