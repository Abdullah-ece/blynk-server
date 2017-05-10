import React from 'react';
import classnames from 'classnames';
import {Timeline, Row, Col, Switch, Select, Button, Popconfirm, Icon} from 'antd';
import Preview from './preview';
import Content from './content';
import {Item} from 'components/UI';
import FormItem from 'components/FormItem';
import {SimpleMatch} from 'services/Filters';
import {EVENT_TYPES, Metadata} from 'services/Products';
import {reduxForm, Field, formValueSelector} from 'redux-form';
import {connect} from 'react-redux';
import _ from 'lodash';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    metadata: state.Product.edit.metadata.fields,
    fields: {
      id: selector(state, 'id'),
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
    metadata: React.PropTypes.array,
    onDelete: React.PropTypes.func,
    tools: React.PropTypes.bool,
    anyTouched: React.PropTypes.bool
  };

  shouldComponentUpdate(nextProps, nextState) {
    return !(_.isEqual(this.props.fields, nextProps.fields)) || !(_.isEqual(this.state, nextState)) || !(_.isEqual(this.props.metadata, nextProps.metadata));
  }

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
      <Select.Option key={field.values.name} value={(field.id).toString()}>{field.values.name}</Select.Option>
    ));
  }

  notificationSelect(props) {

    return (
      <Select mode="multiple"
              onChange={props.input.onChange}
              value={props.input.value || []}
              style={{width: '100%'}}
              placeholder="Select contact"
              allowClear={true}
              filterOption={(value, option) => SimpleMatch(value, option.key)}
              notFoundContent={!props.options.length ? 'No any metadata Contact field with Email' : 'No any field matches your request'}>
        { props.options }
      </Select>
    );
  }

  handleConfirmDelete() {
    this.props.onDelete(this.props.fields);
  }

  toolsPopconfirmDeleteButton() {
    return (
      <Popconfirm title="Are you sure?" overlayClassName="danger"
                  onConfirm={this.handleConfirmDelete.bind(this)}
        // onCancel={this.handleCancelDelete.bind(this)}
                  okText="Yes, Delete"
                  cancelText="Cancel">
        <Button icon="delete" size="small"
          // onClick={this.markAsActive.bind(this)}
        />
      </Popconfirm>
    );
  }

  toolsDeleteButton() {
    return (
      <Button size="small" icon="delete"
              onClick={this.handleConfirmDelete.bind(this)}
      />
    );
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
                    <Field name="emailNotifications"
                           component={this.notificationSelect}
                           options={notificationAvailableMetadataContactFields}/>
                  </Item>
                  <Item label="PUSH to">
                    <Field name="pushNotifications"
                           component={this.notificationSelect}
                           options={notificationAvailableMetadataContactFields}/>
                  </Item>
                </FormItem>
              </Col>
              <Col span={9} offset={1}>
                { this.getChildrenByType(Preview.displayName) }
              </Col>
              { this.props.tools && (
                <Col span={1} className="product-events-event-tools">
                  <Icon type="bars" className="cursor-move"/>

                  { this.props.anyTouched && this.toolsPopconfirmDeleteButton() || this.toolsDeleteButton() }

                  <Button icon="copy" size="small"/>
                </Col>
              )}
            </Row>
            <Row gutter={8}>
              <Col span={15}>
                {/* Notifications there*/}
              </Col>
            </Row>
          </Timeline.Item>
        </Timeline>
      </div>
    );
  }

}

Base.Preview = Preview;
Base.Content = Content;

export default Base;
