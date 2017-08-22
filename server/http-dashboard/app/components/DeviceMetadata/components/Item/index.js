import React from 'react';
import {
  hardcodedRequiredMetadataFieldsNames
} from 'services/Products';
import {Row, Col, Button, Icon} from 'antd';
import './styles.less';

class Item extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    fieldName: React.PropTypes.string,
    onEditClick: React.PropTypes.func
  };

  onEditClick() {
    if (typeof this.props.onEditClick === 'function')
      this.props.onEditClick();
  }

  render() {
    return (
      <div className="device-metadata--item">
        <Row type="flex">
          <Col span={14}>
            {this.props.children}
          </Col>
          <Col span={10} className="device-metadata--item-edit">
            {this.props.fieldName !== hardcodedRequiredMetadataFieldsNames.Manufacturer && (
              <Button type="primary" onClick={this.onEditClick.bind(this)}>
                <Icon type="edit"/>Edit
              </Button>
            ) || (
              <div className="device-metadata--item-edit-no-permission">No permissions to edit</div>
            )}
          </Col>
        </Row>
      </div>
    );
  }

}

export default Item;
