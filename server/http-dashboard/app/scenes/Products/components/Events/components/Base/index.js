import React from 'react';
import classnames from 'classnames';
import {Timeline, Row, Col, Button, Popconfirm, Icon} from 'antd';
import Preview from './preview';
import Content from './content';
import Static from './static';
import Notifications from './notifications';
import {EVENT_TYPES} from 'services/Products';
import {reduxForm, formValueSelector, getFormValues, getFormSyncErrors} from 'redux-form';
import {connect} from 'react-redux';
import _ from 'lodash';
import {SortableHandle} from 'react-sortable-hoc';

@connect((state, ownProps) => {
  const selector = formValueSelector(ownProps.form);
  return {
    metadata: state.Product.edit.metadata.fields,
    fields: {
      id: selector(state, 'id'),
      isNotificationsEnabled: selector(state, 'isNotificationsEnabled')
    },
    formValues: getFormValues(ownProps.form)(state),
    fieldsErrors: getFormSyncErrors(ownProps.form)(state),
  };
})
@reduxForm({
  touchOnChange: true,
  onSubmit: () => {
  }
})
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

  constructor(props) {
    super(props);

    this.state = {
      isActive: false
    };
  }

  shouldComponentUpdate(nextProps, nextState) {
    return !(_.isEqual(this.props.fieldsErrors, nextProps.fieldsErrors)) || !(_.isEqual(this.props.formValues, nextProps.formValues)) || !(_.isEqual(this.state, nextState)) || !(_.isEqual(this.props.metadata, nextProps.metadata));
  }

  handleCancelDelete() {
    this.setState({isActive: false});
  }

  markAsActive() {
    this.setState({isActive: true});
  }

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

  handleConfirmDelete() {
    this.props.onDelete(this.props.fields);
  }

  handleClone() {
    this.props.onClone(this.props.fields);
  }

  toolsPopconfirmDeleteButton() {
    return (
      <Popconfirm title="Are you sure?" overlayClassName="danger"
                  onConfirm={this.handleConfirmDelete.bind(this)}
                  onCancel={this.handleCancelDelete.bind(this)}
                  okText="Yes, Delete"
                  cancelText="Cancel">
        <Button icon="delete" size="small"
                onClick={this.markAsActive.bind(this)}
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

  toolsCloneButton() {
    return (
      <Button icon="copy" size="small"
              onClick={this.handleClone.bind(this)}/>
    );
  }

  toolsDragAndDropButton = SortableHandle(() => <Icon type="bars" className="cursor-move"/>);

  render() {
    const itemClasses = classnames({
      'product-metadata-item': true,
      'product-metadata-item-active': this.state.isActive,
    });

    return (
      <div className={itemClasses}>
        <Timeline>
          <Timeline.Item {...this.getPropsByType(this.props.type)}>
            <Row gutter={8}>
              <Col span={13}>
                { this.getChildrenByType(Content.displayName) }
                <Notifications metadata={this.props.metadata} fields={this.props.fields}/>
              </Col>
              <Col span={9} offset={1}>
                { this.getChildrenByType(Preview.displayName) }
              </Col>
              { this.props.tools && (
                <Col span={1} className="product-events-event-tools">
                  <this.toolsDragAndDropButton />

                  { this.props.anyTouched && this.toolsPopconfirmDeleteButton() || this.toolsDeleteButton() }

                  { this.toolsCloneButton() }
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

Base.Static = Static;
Base.Preview = Preview;
Base.Content = Content;

export default Base;
