import React from 'react';
import {Form} from 'components/UI';
import {Field} from 'redux-form';
import Checkbox from './checkbox';
import Static from './default-static';

class Default extends React.Component {

  static propTypes = {
    placeholder: React.PropTypes.string,
    prefix: React.PropTypes.string,
    isChecked: React.PropTypes.any,
    value: React.PropTypes.any,
    onChange: React.PropTypes.func,
    popconfirm: React.PropTypes.object
  };

  shouldComponentUpdate(nextProps) {
    return this.props.isChecked !== nextProps.isChecked ||
      this.props.value !== nextProps.value ||
      this.props.placeholder !== nextProps.placeholder ||
      this.props.popconfirm !== nextProps.popconfirm ||
      this.props.prefix !== nextProps.prefix;
  }

  capitalizePrefix(name) {
    return name[0].toUpperCase() + name.substr(1);
  }

  render() {

    const props = {
      ...(this.props.onChange ? {onChange: this.props.onChange} : {})
    };

    return (
      <Form.Items offset="small">
        <Form.Item>
          <Field name={`is${this.capitalizePrefix(this.props.prefix)}Enabled`}
                 {...props}
                 placeholder={this.props.placeholder}
                 popconfirm={this.props.popconfirm}
                 component={Checkbox}/>
        </Form.Item>
      </Form.Items>
    );
  }

}

Default.Static = Static;
export default Default;
