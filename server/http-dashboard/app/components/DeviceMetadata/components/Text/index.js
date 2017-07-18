import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import TextModal from './modal';

class Text extends Base {

  constructor(props) {
    super(props);
  }

  isDeviceOwner() {
    const DEVICE_OWNER = 'Device Owner';
    return this.props.data.get('name') === DEVICE_OWNER;
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        {field.get('value') || <i>No Value</i>}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <TextModal form={this.props.form} isDeviceOwner={this.isDeviceOwner()}/>
      </div>
    );
  }

}

export default Text;
