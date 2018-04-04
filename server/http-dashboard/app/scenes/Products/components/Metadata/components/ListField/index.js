import React from 'react';
import FormItem from 'components/FormItem';
import {MetadataField as MetadataFormField, MetadataSelect as MetadataFormSelect} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import Static from './static';

class ListField extends BaseField {

  constructor(props) {
    super(props);

    this.onFocus = this.onFocus.bind(this);
    this.onBlur = this.onBlur.bind(this);

  }

  getPreviewValues() {

    let name = this.props.field.get('name');
    let options = (this.props.field.get('options') && this.props.field.get('options').toJS) ? this.props.field.get('options').toJS() : [];

    let value = Array.isArray(options) ? options.join(', ') : null;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && typeof value === 'string' ? value.trim() : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.Title>List</FormItem.Title>
        <FormItem.Content>
            <MetadataFormField className={`metadata-name-field-${this.props.field.get('id')}`}
                               onFocus={this.onFocus} onBlur={this.onBlur}
                               validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text" placeholder="Field Name" validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
        </FormItem.Content>
        <FormItem.Title>Options</FormItem.Title>
        <FormItem.Content>
          <MetadataFormSelect mode="tags"
                              name={`metaFields.${this.props.metaFieldKey}.options`}
                              defaultValue={[]}
                              placeholder={`Type option and press enter`}
                              values={[]}/>
        </FormItem.Content>
      </FormItem>
    );
  }

}

ListField.Static = Static;
export default ListField;
