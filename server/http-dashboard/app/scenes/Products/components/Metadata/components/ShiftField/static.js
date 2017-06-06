import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import BaseField from '../BaseField';
import Static from './static';
import FieldStub from 'scenes/Products/components/FieldStub';
import {TimeRange} from 'services/Metadata';

class ShiftField extends BaseField.Static {

  getPreviewValues() {
    const name = this.props.name;
    const from = this.props.from;
    const to = this.props.to;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}` : null,
      value: from && typeof from === 'string' && to && typeof to === 'string' ? `From ${from} to ${to}` : null
    };
  }

  component() {

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Time Range</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>From</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>To</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <FieldStub style={{width: '50%'}}>
              {this.props.name}
            </FieldStub>
            <FieldStub style={{width: '25%'}}>
              {this.props.from > 0 ? TimeRange.fromMinutes(this.props.from) : null}
            </FieldStub>
            <FieldStub style={{width: '25%'}}>
              {this.props.to > 0 ? TimeRange.fromMinutes(this.props.to) : null}
            </FieldStub>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

ShiftField.Static = Static;

export default ShiftField;
