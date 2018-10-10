import React      from 'react';
import FormItem   from 'components/FormItem';
import {
  MetadataField as MetadataFormField
}                 from 'components/Form';
import BaseField  from '../BaseField/index';
import Static     from './static';

class ManufacturerField extends BaseField {

  hideMobileAppProvisioning = true;

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);

  }

  getPreviewValues() {
    const name = this.props.field.get('name');
    const value = this.props.field.get('value');

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
    };
  }

  component() {
    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Manufacturer</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <MetadataFormField onFocus={this.onFocus} onBlur={this.onBlur}
                             name={`metaFields.${this.props.metaFieldKey}.value`} type="text" placeholder="Default value (optional)"
                             validateOnBlur={true}
          />
        </FormItem.Content>
      </FormItem>
    );
  }

}

ManufacturerField.Static = Static;
export default ManufacturerField;
