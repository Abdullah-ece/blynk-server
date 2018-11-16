import React from 'react';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import FieldStub from 'scenes/Products/components/FieldStub';

class TemplateIdField extends BaseField.Static {

  DEFAULT_VALUE = 'No Value';

  static propTypes = {
    name: React.PropTypes.string,
    preview: React.PropTypes.any
  };

  getPreviewValues() {
    const name = this.props.name;
    const value = this.props.preview.value;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && Array.isArray(value) ? value.join(', ') :
        value && typeof value === 'string' ? value.trim() : null
    };
  }

  component() {

    let options = this.props.options || [];

    return (
      <FormItem offset={false}>
        <FormItem.Title>Template Id</FormItem.Title>
        <FormItem.Content input>
          <FieldStub>
            {this.props.name}
          </FieldStub>
        </FormItem.Content>
        <br/>
        <FormItem.Title>Options</FormItem.Title>
        <FormItem.Content input>
          {options.map((value) => (
            <FieldStub inline={true} key={value}>
              {value}
            </FieldStub>
          ))}
          {!options.length && (
            <FieldStub inline={true} noValueMessage={`No Options selected`}/>
          )}
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default TemplateIdField;
