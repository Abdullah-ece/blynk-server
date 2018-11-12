import React from 'react';
import FormItem from 'components/FormItem';
import {MetadataField as MetadataFormField} from 'components/Form';
import Validation from 'services/Validation';
import BaseField from '../BaseField/index';
import {Select} from "antd";
import {Field} from "redux-form";
import Static from './static';

class TemplateIdField extends BaseField {

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

  optionsComponent(props) {

    const onChange = (value) => {

      const values = [];

      value.forEach((value = '') => {
        if(value.indexOf(',') >= 0 && value.indexOf(',') < value.length) {
          let list = value.replace(/(\r\n|\n|\r)/gm,"").split(',');

          list.map((item) => values.push(item));
        } else {
          values.push(value);
        }
      });

      props.input.onChange(values);
    };

    const getValue = () => {
      return Array.isArray(props.input.value) ? props.input.value : [];
    };

    return (
      <Select style={{width: '100%', marginBottom: '24px'}}
              mode="tags"
              onChange={onChange}
              value={getValue()}
              defaultValue={[]}
              placeholder={`Type option and press enter`}/>
    );
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.Title>Template Id</FormItem.Title>
        <FormItem.Content>
            <MetadataFormField className={`metadata-name-field-${this.props.field.get('id')}`}
                               onFocus={this.onFocus} onBlur={this.onBlur}
                               validateOnBlur={true} name={`metaFields.${this.props.metaFieldKey}.name`} type="text" placeholder="Field Name" validate={[
              Validation.Rules.required, Validation.Rules.metafieldName,
            ]}/>
        </FormItem.Content>
        <FormItem.Title>Options</FormItem.Title>
        <FormItem.Content>
          <Field component={this.optionsComponent} name={`metaFields.${this.props.metaFieldKey}.options`}/>
        </FormItem.Content>
      </FormItem>
    );
  }

}

TemplateIdField.Static = Static;
export default TemplateIdField;
