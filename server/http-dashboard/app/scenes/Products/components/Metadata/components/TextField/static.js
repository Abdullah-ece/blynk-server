import React from 'react';
import {Input} from 'antd';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';

class TextField extends BaseField.Static {
  component() {
    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>String</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content input>
          <Input.Group compact>
            <div className="product-metadata-static-field">Some</div>
            <div className="product-metadata-static-field no-value">No value</div>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default TextField;
