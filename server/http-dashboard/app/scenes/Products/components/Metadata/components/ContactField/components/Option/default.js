import React from 'react';
import {Form} from 'components/UI';
import {Field} from 'redux-form';
import {Checkbox} from 'antd';
import Static from './default-static';

class Default extends React.Component {

  static propTypes = {
    placeholder: React.PropTypes.string,
    prefix: React.PropTypes.string
  };

  capitalizePrefix(name) {
    return name[0].toUpperCase() + name.substr(1);
  }

  checkbox(props) {
    return (
      <Checkbox onChange={props.input.onChange} checked={!!props.input.value}>{props.placeholder}</Checkbox>
    );
  }

  render() {
    return (
      <Form.Items offset="small">
        <Form.Item>
          <Field name={`is${this.capitalizePrefix(this.props.prefix)}Enabled`}
                 placeholder={this.props.placeholder}
                 component={this.checkbox}/>
        </Form.Item>
      </Form.Items>
    );
  }

}

Default.Static = Static;
export default Default;
