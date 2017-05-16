import React from 'react';
import {Input} from 'antd';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import FieldStub from 'scenes/Products/components/FieldStub';

class NumberFieldStatic extends BaseField.Static {

  static propTypes = {
    name: React.PropTypes.string,
    value: React.PropTypes.any
  };

  getPreviewValues() {
    const name = this.props.name;
    const value = this.props.value;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: value && !isNaN(Number(value)) ? Number(value) : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Number</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content input>
          <Input.Group compact>
            <FieldStub style={{width: '50%'}}>
              {this.props.name}
            </FieldStub>
            <FieldStub style={{width: '50%'}}>
              {this.props.value}
            </FieldStub>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default NumberFieldStatic;
