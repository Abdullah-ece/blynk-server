import React from 'react';
import {Metadata, Currency} from 'services/Products';
import {Button, Menu, Dropdown, Icon} from 'antd';
import './styles.less';

class AddNewMetadataField extends React.Component {

  static propTypes = {
    onFieldAdd: React.PropTypes.func
  };

  typesPredefinedValues = {
    [Metadata.Fields.COST]: {
      values: {
        currency: Currency.USD.key
      }
    }
  };

  menu = (
    <Menu selectable={false} onClick={this.handleMenuClick.bind(this)}>
      <Menu.Item key={Metadata.Fields.COST}>
        Cost
      </Menu.Item>
      <Menu.Item key={Metadata.Fields.COORDINATES}>
        Coordinates
      </Menu.Item>
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
      <div className="products-add-new-metadata-field">
        <div className="products-add-new-metadata-field-title">+ Add new Metadata Field:</div>
        <div className="products-add-new-metadata-field-fields">
          <Button type="dashed" onClick={this.addField.bind(this, Metadata.Fields.TEXT)}>Text</Button>
          <Button type="dashed" onClick={this.addField.bind(this, Metadata.Fields.NUMBER)}>Number</Button>
          <Button type="dashed" onClick={this.addField.bind(this, Metadata.Fields.UNIT)}>Unit</Button>
          <Button type="dashed" onClick={this.addField.bind(this, Metadata.Fields.TIME)}>Time</Button>
          <Button type="dashed" onClick={this.addField.bind(this, Metadata.Fields.SHIFT)}>Shift</Button>
        </div>
        <div className="products-add-new-metadata-field-other-fields">
          <Dropdown overlay={this.menu}>
            <a className="ant-dropdown-link" href="javascript:void(0);">
              Other Metadata Types <Icon type="down"/>
            </a>
          </Dropdown>
        </div>
      </div>
    );
  }
}

export default AddNewMetadataField;
