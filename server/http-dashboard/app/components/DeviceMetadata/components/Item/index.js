import React from 'react';
import {Row, Col, Button, Icon} from 'antd';
import './styles.less';

class Item extends React.Component {

  static propTypes = {
    children: React.PropTypes.any
  };

  render() {
    return (
      <div className="device-metadata--item">
        <Row type="flex">
          <Col span={14}>
            {this.props.children}
          </Col>
          <Col span={10} className="device-metadata--item-edit">
            <Button type="primary"><Icon type="edit"/>Edit</Button>
            {/*<div className="device-metadata--item-edit-no-permission">No permissions to edit</div>*/}
          </Col>
        </Row>
      </div>
    );
  }

}

export default Item;
