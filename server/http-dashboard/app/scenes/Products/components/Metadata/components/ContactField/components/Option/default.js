import React from 'react';
import {Form} from 'components/UI';
import {Field} from 'redux-form';
import {Checkbox} from 'antd';

export default class Default extends React.Component {

  static propTypes = {
    placeholder: React.PropTypes.string,
    prefix: React.PropTypes.string
  };

  checkbox(props) {
    return (
      <Checkbox onChange={props.input.onChange} checked={!!props.input.value}>{props.placeholder}</Checkbox>
    );
  }

  render() {
    return (
      <Form.Items offset="small">
        <Form.Item>
          <Field name={`${this.props.prefix}Check`} placeholder={this.props.placeholder} component={this.checkbox}/>
        </Form.Item>
      </Form.Items>
    );
  }

}
