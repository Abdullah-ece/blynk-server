import React from 'react';
import {Form} from 'components/UI';
import {Field} from 'redux-form';
import Default from './default';
import Static from './input-static';
import Checkbox from './checkbox';

class Input extends Default {

  static propTypes = {
    placeholder: React.PropTypes.string,
    isChecked: React.PropTypes.any,
    value: React.PropTypes.any,
    onChange: React.PropTypes.func,
    popconfirm: React.PropTypes.object,
    onFocus: React.PropTypes.func,
    onBlur: React.PropTypes.func,
  };

  shouldComponentUpdate(nextProps) {
    return this.props.isChecked !== nextProps.isChecked ||
      this.props.value !== nextProps.value ||
      this.props.popconfirm !== nextProps.popconfirm ||
      this.props.placeholder !== nextProps.placeholder;
  }

  render() {

    const props = {
      ...(this.props.onChange ? {onChange: this.props.onChange} : {})
    };

    return (
      <Form.Items layout="inline">
        <Form.Item>
          <Field {...props} name={`is${this.capitalizePrefix(this.props.prefix)}Enabled`}
                 popconfirm={this.props.popconfirm}
                 component={Checkbox}/>
        </Form.Item>
        <Form.Item>
          <Form.Input onFocus={this.props.onFocus} onBlur={this.props.onBlur} disabled={!this.props.isChecked}
                      name={`${this.props.prefix}`}
                      placeholder={this.props.placeholder}/>
        </Form.Item>
      </Form.Items>
    );
  }

}

Input.Static = Static;
export default Input;
