import React from 'react';
import {Button, Menu, Dropdown, Icon} from 'antd';

class AddNewMetadataField extends React.Component {

  static propTypes = {
    onFieldAdd: React.PropTypes.func
  };

  menu = () => (
    <Menu selectable={false} onClick={this.handleMenuClick.bind(this)}>
      {
        this.fields.filter((item, key) => key >= 5).map((field) => (
          <Menu.Item key={field.type}>
            {field.title}
          </Menu.Item>
        ))
      }
    </Menu>
  );

  handleMenuClick({key}) {
    this.addField(key);
  }

  addField(type) {
    this.props.onFieldAdd({
      type: type,
      ...this.typesPredefinedValues && this.typesPredefinedValues[type]
    });
  }

  render() {

    return (
      <div className="products-add-new-field">
        <div className="products-add-new-field-title">+ Add new {this.title} Field:</div>
        <div className="products-add-new-field-fields">
          {
            this.fields.filter((item, key) => key < 5).map((field, key) => (
              <Button type="dashed" key={key} onClick={this.addField.bind(this, field.type)}>{field.title}</Button>
            ))
          }
        </div>
        <div className="products-add-new-field-other-fields">
          <Dropdown overlay={this.menu()}>
            <a className="ant-dropdown-link" href="javascript:void(0);">
              Other {this.title} Types <Icon type="down"/>
            </a>
          </Dropdown>
        </div>
      </div>
    );
  }
}

export default AddNewMetadataField;
