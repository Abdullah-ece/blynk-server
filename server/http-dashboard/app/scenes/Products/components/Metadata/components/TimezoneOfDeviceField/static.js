import React      from 'react';
import BaseField  from '../BaseField';
import FormItem   from 'components/FormItem';
import FieldStub  from 'scenes/Products/components/FieldStub';

class TimezoneOfDeviceField extends BaseField.Static {

  static propTypes = {
    name: React.PropTypes.string,
    value: React.PropTypes.any
  };

  getPreviewValues() {
    const name = this.props.name;
    const value = this.props.value;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.Title>Device Timezone</FormItem.Title>
        <FormItem.Content input>
          <FieldStub>
            {this.props.value}
          </FieldStub>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default TimezoneOfDeviceField;
