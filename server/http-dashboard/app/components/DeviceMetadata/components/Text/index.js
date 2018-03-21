import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import TextModal from './modal';
import {hardcodedRequiredMetadataFieldsNames} from 'services/Products';

class Text extends Base {

  constructor(props) {
    super(props);
  }

  isDeviceOwner() {
    return this.props.data.name === hardcodedRequiredMetadataFieldsNames.DeviceOwner;
  }

  isTimezoneOfDevice() {
    return this.props.data.name === hardcodedRequiredMetadataFieldsNames.TimezoneOfTheDevice;
  }

  getPreviewComponent() {
    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.name}</Fieldset.Legend>
        {field.value || <i>No Value</i>}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <TextModal form={this.props.form} isDeviceOwner={this.isDeviceOwner()}
                   isTimezoneOfDevice={this.isTimezoneOfDevice()}/>
      </div>
    );
  }

}

export default Text;
