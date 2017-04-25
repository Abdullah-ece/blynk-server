import React from 'react';
import {Metadata} from 'services/Products';
import {Button, Menu, Dropdown, Icon} from 'antd';
import './styles.less';

class AddNewMetadataField extends React.Component {

  static propTypes = {
    onFieldAdd: React.PropTypes.func
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
      type: type
    });
  }

  addTextField() {
    this.props.onFieldAdd({
      type: Metadata.Fields.TEXT
    });
  }

  addNumberField() {
    this.props.onFieldAdd({
      type: Metadata.Fields.NUMBER
    });
  }

  addCostField() {
    this.props.onFieldAdd({
      type: Metadata.Fields.COST
    });
  }

  addTimeField() {
    this.props.onFieldAdd({
      type: Metadata.Fields.TIME
    });
  }

  addShiftField() {
    this.props.onFieldAdd({
      type: Metadata.Fields.SHIFT
    });
  }

  addDateField() {
    this.props.onFieldAdd({
      type: Metadata.Fields.DATE
    });
  }

  addCoordinatesField() {
    this.props.onFieldAdd({
      type: Metadata.Fields.COORDINATES
    });
  }

  addUnitField() {
    this.props.onFieldAdd({
      type: Metadata.Fields.UNIT
    });
  }

  addContactField() {
    this.props.onFieldAdd({
      type: Metadata.Fields.CONTACT
    });
  }

  render() {
    return (
      <div className="products-add-new-metadata-field">
        <div className="products-add-new-metadata-field-title">+ Add new Metadata Field:</div>
        <div className="products-add-new-metadata-field-fields">
          <Button type="dashed" onClick={this.addTextField.bind(this)}>Text</Button>
          <Button type="dashed" onClick={this.addNumberField.bind(this)}>Number</Button>
          <Button type="dashed" onClick={this.addUnitField.bind(this)}>Unit</Button>
          <Button type="dashed" onClick={this.addTimeField.bind(this)}>Time</Button>
          <Button type="dashed" onClick={this.addShiftField.bind(this)}>Shift</Button>
          {/*<Button type="dashed" onClick={this.addDateField.bind(this)}>Date</Button>*/}
          {/*<Button type="dashed" onClick={this.addCostField.bind(this)}>Cost</Button>*/}
          {/*<Button type="dashed" onClick={this.addCoordinatesField.bind(this)}>Coordinates</Button>*/}
          {/*<Button type="dashed" onClick={this.addContactField.bind(this)}>Contact</Button>*/}
          {/*<Button type="dashed">Contact</Button>*/}
        </div>
        {/* @todo uncomment when add additional types of metadata*/}
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
