import React from 'react';
import {
  isUserAbleToEdit
} from "services/Roles";
import {Row, Col, Button, Icon} from 'antd';
import './styles.less';

class Item extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    fieldName: React.PropTypes.string,
    onEditClick: React.PropTypes.func,
    fieldRole: React.PropTypes.string,
    userRole: React.PropTypes.string,
  };

  constructor(props){
    super(props);
    this.onEditClick = this.onEditClick.bind(this);
  }
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
            {isUserAbleToEdit(this.props.userRole, this.props.fieldRole) && (
              <Button type="primary" onClick={this.onEditClick}>
                <Icon type="edit"/>Edit
              </Button>
            )}
          </Col>
        </Row>
      </div>
    );
  }

}

export default Item;
