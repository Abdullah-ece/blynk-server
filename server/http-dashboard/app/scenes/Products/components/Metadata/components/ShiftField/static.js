import React from 'react';
import FormItem from 'components/FormItem';
import {Input} from 'antd';
import BaseField from '../BaseField';
import Static from './static';
import classnames from 'classnames';

class ShiftField extends BaseField.Static {

  getPreviewValues() {
    const name = this.props.name;
    const from = this.props.from;
    const to = this.props.to;

    return {
      name: name && typeof name === 'string' ? `${name.trim()}:` : null,
      value: from && typeof from === 'string' && to && typeof to === 'string' ? `From ${from} to ${to}` : null
    };
  }

  component() {

    const valueClassNames = classnames({
      'product-metadata-static-field': true,
      'no-value': !this.props.from && !this.props.to
    });

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Time Range</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>From</FormItem.Title>
          <FormItem.Title style={{width: '25%'}}>To</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content>
          <Input.Group compact>
            <div className="product-metadata-static-field" style={{width: '200%'}}>
              {this.props.name}
            </div>
            <div className={valueClassNames}>
              {this.props.from || this.DEFAULT_VALUE}
            </div>
            <div className={valueClassNames}>
              {this.props.to || this.DEFAULT_VALUE}
            </div>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

ShiftField.Static = Static;

export default ShiftField;
