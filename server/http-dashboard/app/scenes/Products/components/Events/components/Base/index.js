import React from 'react';
import classnames from 'classnames';
import {Timeline, Row, Col} from 'antd';
import Preview from './preview';
import Content from './content';

class Base extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
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

  getChildrenByType(type, children = this.props.children) {

    if (!children) return null;

    let element;
    if (Array.isArray(children)) {
      element = children.filter((child) => !!this.getChildrenByType(type, child));
    } else if (children.type.displayName === type) {
      return children;
    }

    return element || null;
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
                { this.getChildrenByType(Content.displayName) }
              </Col>
              <Col span={9}>
                { this.getChildrenByType(Preview.displayName) }
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

Base.Preview = Preview;
Base.Content = Content;

export default Base;
