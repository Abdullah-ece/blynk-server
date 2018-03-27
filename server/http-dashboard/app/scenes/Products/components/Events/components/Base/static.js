import React from 'react';
import classnames from 'classnames';
import {Timeline, Row, Col} from 'antd';
import Preview from './preview';
import Content from './content';
import Static from './static';
import Notifications from './notifications';
import {EVENT_TYPES} from 'services/Products';
import {connect} from 'react-redux';

@connect((state) => ({
  metadata: state.Product.edit.metadata.fields
}))
class Base extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    type: React.PropTypes.string,
    fields: React.PropTypes.object,
    metadata: React.PropTypes.array,
    onClone: React.PropTypes.func,
    onDelete: React.PropTypes.func,
    tools: React.PropTypes.bool,
    anyTouched: React.PropTypes.bool,
    formValues: React.PropTypes.any,
    fieldsErrors: React.PropTypes.any,
  };

  getPropsByType(type) {

    if (type === EVENT_TYPES.ONLINE) {
      return {
        color: 'gray'
      };
    }

    if (type === EVENT_TYPES.OFFLINE) {
      return {
        color: 'gray'
      };
    }

    if (type === EVENT_TYPES.INFO) {
      return {
        color: '#23be1b' // primary
      };
    }

    if (type === EVENT_TYPES.WARNING) {
      return {
        color: '#eb7a21' // warning
      };
    }

    if (type === EVENT_TYPES.CRITICAL) {
      return {
        color: '#da1d4e' // critical
      };
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
      'static': true
    });

    return (
      <div className={itemClasses}>
        <Timeline>
          <Timeline.Item {...this.getPropsByType(this.props.type)}>
            <Row gutter={8}>
              <Col span={13}>
                { this.getChildrenByType(Content.displayName) }
                <Notifications.Static fields={this.props.fields}/>
              </Col>
              <Col span={9} offset={1}>
                { this.getChildrenByType(Preview.displayName) }
              </Col>
            </Row>
          </Timeline.Item>
        </Timeline>
      </div>
    );
  }

}

Base.Static = Static;
Base.Preview = Preview;
Base.Content = Content;

export default Base;
