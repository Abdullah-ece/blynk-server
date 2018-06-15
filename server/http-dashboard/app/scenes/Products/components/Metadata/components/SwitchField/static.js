import React from 'react';
import {Input} from 'antd';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import FieldStub from 'scenes/Products/components/FieldStub';

class SwitchField extends BaseField.Static {

  DEFAULT_VALUE = 'No Value';

  static propTypes = {
    name: React.PropTypes.string,
    from: React.PropTypes.any,
    to: React.PropTypes.any,
    role: React.PropTypes.any
  };

  getPreviewValues() {
    const name = this.props.name;
    const from = this.props.from;
    const to = this.props.to;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: typeof from === 'string' && typeof to === 'string' ? `${from} / ${to}` : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '33.3%'}}>String</FormItem.Title>
          <FormItem.Title style={{width: '33.3%'}}>Option A</FormItem.Title>
          <FormItem.Title style={{width: '33.3%'}}>Option B</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content input>
          <Input.Group compact>
            <FieldStub style={{width: '33.3%'}}>
              {this.props.name}
            </FieldStub>
            <FieldStub style={{width: '33.3%'}}>
              {this.props.from}
            </FieldStub>
            <FieldStub style={{width: '33.3%'}}>
              {this.props.to}
            </FieldStub>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default SwitchField;
