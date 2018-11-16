import React from 'react';
import Base from '../Base';
import {Fieldset, LinearIcon} from 'components';
import ContactModal from './modal';

class Contact extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    const data = [];

    if (field.firstName && field.lastName && field.isLastNameEnabled && field.isFirstNameEnabled)
      data.push(`${field.firstName} ${field.lastName}`);

    if (field.firstName && !field.lastName && !field.isLastNameEnabled && field.isFirstNameEnabled)
      data.push(`${field.firstName}`);

    if (field.lastName && !field.firstName && field.isLastNameEnabled && !field.isFirstNameEnabled)
      data.push(`${field.lastName}`);

    if (field.email && field.isEmailEnabled)
      data.push(<a className={'txt_link'} href={"mailto:" + field.email}>{field.email}</a>);

    if (field.phone && field.isPhoneEnabled)
      data.push(`${field.phone}`);

    if (field.streetAddress && field.isStreetAddressEnabled)
      data.push(`${field.streetAddress}`);

    if (field.city && field.isCityEnabled)
      data.push(`${field.city}`);

    if (field.state && field.isStateEnabled)
      data.push(`${field.state}`);

    if (field.zip && field.isZipEnabled)
      data.push(`${field.zip}`);

    return (
      <Fieldset>
        <Fieldset.Legend type="dark"> <LinearIcon type={field.icon || 'cube'}/> {field.name}         </Fieldset.Legend>
        {!data.length && <i>No Value</i>}
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
