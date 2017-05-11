import React from 'react';
import classnames from 'classnames';
import {Timeline, Row, Col, Switch, Select} from 'antd';
import Tools from './tools';
import Preview from './preview';
import Content from './content';
import {Item} from 'components/UI';
import FormItem from 'components/FormItem';
import {EVENT_TYPES, Metadata} from 'services/Products';
import {reduxForm, Field, formValueSelector} from 'redux-form';
import {connect} from 'react-redux';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    metadata: state.Product.edit.metadata.fields,
    fields: {
      isNotificationsEnabled: selector(state, 'isNotificationsEnabled')
    }
  };
})
@reduxForm()
class Base extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    type: React.PropTypes.string,
    fields: React.PropTypes.object,
    metadata: React.PropTypes.array
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

  switch(props) {
    return <Switch size="small" onChange={props.input.onChange} checked={!!props.input.value}/>;
  }

  getMetadataContactFieldsWithEmail() {
    return this.props.metadata.filter((field) => {
      return field.type === Metadata.Fields.CONTACT && field.values && field.values.isEmailEnabled;
    }).map((field) => (
      <Select.Option key={field.id}>{field.values.name}</Select.Option>
    ));
  }

  render() {
    const itemClasses = classnames({
      'product-metadata-item': true,
      'product-metadata-item-active': /*this.state.isActive*/ false,
    });

    let notificationAvailableMetadataContactFields = this.getMetadataContactFieldsWithEmail();

    return (
      <div className={itemClasses}>
        <Timeline>
          <Timeline.Item {...this.getPropsByType(this.props.type)}>
            <Row gutter={8}>
              <Col span={13}>
                { this.getChildrenByType(Content.displayName) }
                <Item offset="small">
                  <Field name="isNotificationsEnabled" component={this.switch}/> Notifications
                </Item>
                <FormItem visible={!!this.props.fields && !!this.props.fields.isNotificationsEnabled}>
                  <Item label="E-mail to" offset="normal">
                    <Select mode="tags" style={{width: '100%'}} placeholder="Select contact"
                            notFoundContent="No any metadata contact field with Email">
                      { notificationAvailableMetadataContactFields }
                    </Select>
                  </Item>
                  <Item label="PUSH to">
                    <Select mode="tags" style={{width: '100%'}} placeholder="Select contact"
                            notFoundContent="No any metadata contact field with Email">
                      { notificationAvailableMetadataContactFields }
                    </Select>
                  </Item>
                </FormItem>
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
