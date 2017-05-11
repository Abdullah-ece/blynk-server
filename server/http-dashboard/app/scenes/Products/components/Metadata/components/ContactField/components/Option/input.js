import React from 'react';
import {Form} from 'components/UI';
import {Field} from 'redux-form';
import Default from './default';
import Static from './input-static';
import Checkbox from './checkbox';

class Input extends Default {

  static propTypes = {
    placeholder: React.PropTypes.string,
    isChecked: React.PropTypes.bool
  };

  shouldComponentUpdate(nextProps) {
    return this.props.isChecked !== nextProps.isChecked ||
      this.props.value !== nextProps.value ||
      this.props.placeholder !== nextProps.placeholder
  }

  render() {
    return (
      <Form.Items layout="inline">
        <Form.Item>
          <Field name={`is${this.capitalizePrefix(this.props.prefix)}Enabled`} component={Checkbox}/>
        </Form.Item>
        <Form.Item>
          <Form.Input disabled={!this.props.isChecked} name={`${this.props.prefix}`}
                      placeholder={this.props.placeholder}/>
        </Form.Item>
      </Form.Items>
    );
  }

}

Input.Static = Static;
export default Input;
