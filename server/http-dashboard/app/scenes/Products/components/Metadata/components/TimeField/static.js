import React from 'react';
import {Input} from 'antd';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import FieldStub from 'scenes/Products/components/FieldStub';

class TextField extends BaseField.Static {

  static propTypes = {
    name: React.PropTypes.string,
    time: React.PropTypes.any
  };

  getPreviewValues() {
    const name = this.props.name;
    const time = this.props.time;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: time && typeof time === 'string' ? `${time}` : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Time</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content input>
          <Input.Group compact>
            <FieldStub style={{width: '50%'}}>
              {this.props.name}
            </FieldStub>
            <FieldStub style={{width: '50%'}}>
              {this.props.time}
            </FieldStub>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default TextField;
