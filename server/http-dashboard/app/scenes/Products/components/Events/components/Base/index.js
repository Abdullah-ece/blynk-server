import React from 'react';
import classnames from 'classnames';
import {Timeline, Row, Col} from 'antd';

class Base extends React.Component {

  static propTypes = {
    type: React.PropTypes.string
  };

  TYPES = {
    ONLINE: 'online',
    OFFLINE: 'offline',
    INFO: 'info',
    WARNING: 'warning',
    ALERT: 'alert'
  };

  getPropsByType(type) {

    if (type === this.TYPES.ONLINE) {
      return {
        color: 'green'
      }
    }

    if (type === this.TYPES.OFFLINE) {
      return {
        color: 'gray'
      }
    }

    if (type === this.TYPES.INFO) {
      return {
        color: 'blue'
      }
    }

    if (type === this.TYPES.WARNING) {
      return {
        color: 'orange'
      }
    }

    if (type === this.TYPES.ALERT) {
      return {
        color: 'red'
      }
    }

    return {};
  }

  render() {
    const itemClasses = classnames({
      'product-metadata-item': true,
      'product-metadata-item-active': /*this.state.isActive*/ false,
    });

    return (
      <div className={itemClasses}>
        <Timeline>
          <Timeline.Item {...this.getPropsByType(this.props.type)}>
            <Row gutter={8}>
              <Col span={15}>
                Content layout
              </Col>
              <Col span={9}>
                Preview layout
              </Col>
            </Row>
            <Row gutter={8}>
              <Col span={15}>
                {/* Notifications there*/}
              </Col>
            </Row>
          </Timeline.Item>
        </Timeline>
      </div>
    )
  }

}

export default Base;
