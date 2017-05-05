import React from 'react';
import {Form as BaseForm} from 'antd';
import Item from './components/Item';
import Items from './components/Items';
import Checkbox from './components/Checkbox';
import Switch from './components/Switch';
import ItemsGroup from './components/ItemsGroup';
import Field from './components/Field';
import Input from './components/Input';
import {reduxForm} from 'redux-form';

@reduxForm()
class Form extends React.Component {

  static propTypes = {
    layout: React.PropTypes.string,
    children: React.PropTypes.any
  };

  render() {
    const props = {};

    return (
      <BaseForm {...props} layout={this.props.layout}>
        {this.props.children}
      </BaseForm>
    );
  }
}

Form.Switch = Switch;
Form.Checkbox = Checkbox;
Form.Field = Field;
Form.Items = Items;
Form.Item = Item;
Form.ItemsGroup = ItemsGroup;
Form.Input = Input;

export default Form;
