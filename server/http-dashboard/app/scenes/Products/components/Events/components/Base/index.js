import React from 'react';
import classnames from 'classnames';
import {Timeline, Row, Col, Switch, Select} from 'antd';
import Tools from './tools';
import Preview from './preview';
import Content from './content';
import {Item} from 'components/UI';
import {EVENT_TYPES} from 'services/Products';
import {reduxForm} from 'redux-form';

@reduxForm()
class Base extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    type: React.PropTypes.string
  };

  getPropsByType(type) {

    if (type === EVENT_TYPES.ONLINE) {
      return {
        color: 'green'
      };
    }

    if (type === EVENT_TYPES.OFFLINE) {
      return {
        color: 'gray'
      };
    }

    if (type === EVENT_TYPES.INFO) {
      return {
        color: 'blue'
      };
    }

    if (type === EVENT_TYPES.WARNING) {
      return {
        color: 'orange'
      };
    }

    if (type === EVENT_TYPES.CRITICAL) {
      return {
        color: 'red'
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
      'product-metadata-item-active': /*this.state.isActive*/ false,
    });

    return (
      <div className={itemClasses}>
        <Timeline>
          <Timeline.Item {...this.getPropsByType(this.props.type)}>
            <Row gutter={8}>
              <Col span={13}>
                { this.getChildrenByType(Content.displayName) }
                <Item offset="small">
                  <Switch size="small"/> Notifications
                </Item>
                <Item label="E-mail to" offset="normal">
                  <Select mode="tags" style={{width: '100%'}} placeholder="Select contact">
                    <Select.Option value="Location Owner">Location Owner</Select.Option>
                    <Select.Option value="Product Owner">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 2">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 3">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 4">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 5">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 6">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 7">Product Owner</Select.Option>
                  </Select>
                </Item>
                <Item label="PUSH to">
                  <Select mode="tags" style={{width: '100%'}} placeholder="Select contact">
                    <Select.Option value="Location Owner">Location Owner</Select.Option>
                    <Select.Option value="Product Owner">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 2">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 3">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 4">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 5">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 6">Product Owner</Select.Option>
                    <Select.Option value="Product Owner 7">Product Owner</Select.Option>
                  </Select>
                </Item>
              </Col>
              <Col span={9} offset={1}>
                { this.getChildrenByType(Preview.displayName) }
              </Col>
              <Col span={1}>
                { this.getChildrenByType(Tools.displayName) }
              </Col>
            </Row>
          </Timeline.Item>
        </Timeline>
      </div>
    );
  }

}

Base.Tools = Tools;
Base.Preview = Preview;
Base.Content = Content;

export default Base;
