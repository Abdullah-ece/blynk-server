import React from 'react';
import {Input} from 'antd';
import BaseField from '../BaseField';
import FormItem from 'components/FormItem';
import classnames from 'classnames';

class TextField extends BaseField.Static {

  DEFAULT_VALUE = 'No Value';

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

    const valueClassNames = classnames({
      'product-metadata-static-field': true,
      'no-value': !this.props.time
    });

    return (
      <FormItem offset={false}>
        <FormItem.TitleGroup>
          <FormItem.Title style={{width: '50%'}}>Time</FormItem.Title>
          <FormItem.Title style={{width: '50%'}}>Value</FormItem.Title>
        </FormItem.TitleGroup>
        <FormItem.Content input>
          <Input.Group compact>
            <div className="product-metadata-static-field">
              {this.props.name}
            </div>
            <div className={valueClassNames}>
              {this.props.time || this.DEFAULT_VALUE}
            </div>
          </Input.Group>
        </FormItem.Content>
      </FormItem>
    );
  }
}

export default TextField;
