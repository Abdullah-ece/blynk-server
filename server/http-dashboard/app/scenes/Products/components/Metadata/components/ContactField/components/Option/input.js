import React from 'react';
import {Form} from 'components/UI';
import {Field} from 'redux-form';
import Default from './default';

export default class Input extends Default {

  static propTypes = {
    placeholder: React.PropTypes.string,
    isChecked: React.PropTypes.bool
  };

  render() {
    return (
      <Form.Items layout="inline">
        <Form.Item>
          <Field name={`${this.props.prefix}Check`} component={this.checkbox}/>
        </Form.Item>
        <Form.Item>
          <Form.Input disabled={!this.props.isChecked} name={`${this.props.prefix}Input`}
                      placeholder={this.props.placeholder}/>
        </Form.Item>
      </Form.Items>
    );
  }

}
