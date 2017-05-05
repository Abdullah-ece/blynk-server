import React from 'react';
import {Form as BaseForm} from 'antd';
import Item from './components/Item';
import Field from './components/Field';
import Input from './components/Input';
import {reduxForm} from 'redux-form';

@reduxForm()
class Form extends React.Component {

  static propTypes = {
    children: React.PropTypes.object
  };

  render() {
    const props = {};

    return (
      <BaseForm {...props}>
        {this.props.children}
      </BaseForm>
    );
  }
}

Form.Field = Field;
Form.Item = Item;
Form.Input = Input;

export default Form;
