import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import ContactModal from './modal';

class Contact extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    const data = [];

    if (field.get('firstName') && field.get('lastName') && field.get('isLastNameEnabled') && field.get('isFirstNameEnabled'))
      data.push(`${field.get('firstName')} ${field.get('lastName')}`);

    if (field.get('firstName') && !field.get('lastName') && !field.get('isLastNameEnabled') && field.get('isFirstNameEnabled'))
      data.push(`${field.get('firstName')}`);

    if (field.get('lastName') && !field.get('firstName') && field.get('isLastNameEnabled') && !field.get('isFirstNameEnabled'))
      data.push(`${field.get('lastName')}`);

    if (field.get('email') && field.get('isEmailEnabled'))
      data.push(`${field.get('email')}`);

    if (field.get('phone') && field.get('isPhoneEnabled'))
      data.push(`${field.get('phone')}`);

    if (field.get('streetAddress') && field.get('isStreetAddressEnabled'))
      data.push(`${field.get('streetAddress')}`);

    if (field.get('city') && field.get('isCityEnabled'))
      data.push(`${field.get('city')}`);

    if (field.get('state') && field.get('isStateEnabled'))
      data.push(`${field.get('state')}`);

    if (field.get('zip') && field.get('isZipEnabled'))
      data.push(`${field.get('zip')}`);

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        { !data.length && <i>No Value</i>}
        {
          data.map((field, key) => {
            return (<p key={key}>{field}</p>);
          })
        }
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <ContactModal form={this.props.form} data={this.props.data}/>
      </div>
    );
  }

}

export default Contact;
