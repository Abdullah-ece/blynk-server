import React from 'react';
import {Form} from 'components/UI';
import {Checkbox} from 'antd';

export default class Default extends React.Component {

  static propTypes = {
    placeholder: React.PropTypes.string,
    prefix: React.PropTypes.string
  };

  capitalizePrefix(name) {
    return name[0].toUpperCase() + name.substr(1);
  }

  checkbox(props) {
    return (
      <Checkbox disabled={true} checked={!!props.value}>{props.placeholder}</Checkbox>
    );
  }

  render() {
    return (
      <Form.Items offset="small">
        <Form.Item className="contact-field-static">
          {this.checkbox(this.props)}
        </Form.Item>
      </Form.Items>
    );
  }

}
