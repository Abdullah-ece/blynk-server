import React from 'react';
import {Input} from 'antd';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import FieldStub from 'scenes/Products/components/FieldStub';
import {Time} from 'services/Metadata';

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
      value: !isNaN(Number(time)) ? `${Time.fromTimestamp(time)}` : '--:--'
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
              {!isNaN(Number(this.props.time)) ? Time.fromTimestamp(this.props.time || 0) : null}
            </FieldStub>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default TextField;
