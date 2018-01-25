import React from 'react';
import Scroll from 'react-scroll';
import classnames from 'classnames';
import {Timeline, Row, Col, Button, Popconfirm, Icon} from 'antd';
import Preview from './preview';
import Content from './content';
import Static from './static';
import Notifications from './notifications';
import {
  EVENT_TYPES,
  Metadata,
  FORMS,
  hardcodedRequiredMetadataFieldsNames,
  isEventPristine,
} from 'services/Products';
// import _ from 'lodash';
import {SortableHandle} from 'react-sortable-hoc';
import PropTypes from 'prop-types';
import {getFormValues} from 'redux-form';
import {List, fromJS} from 'immutable';
import {connect} from 'react-redux';

@connect((state) => ({
  contactMetaFields: (() => {
    /* get MetaFields type Contact and Hardcoded Device Owner to display those metaFields on Notifications select */
    const formValues = fromJS(getFormValues(FORMS.PRODUCTS_PRODUCT_CREATE)(state));

    return fromJS((formValues.get('metaFields') || []).filter(
      (metaField) => (
        (metaField.get('type') === Metadata.Fields.CONTACT && metaField.get('isEmailEnabled')) ||
        (metaField.get('name') === hardcodedRequiredMetadataFieldsNames.DeviceOwner && !!metaField.get('value'))
      )
    ));
  })()
}))
class Base extends React.Component {

  static propTypes = {
    contactMetaFields: PropTypes.instanceOf(List),
    children: PropTypes.any,
    type: PropTypes.string,
    field: PropTypes.object,
    metadata: PropTypes.array,
    onClone: PropTypes.func,
    onDelete: PropTypes.func,
    tools: PropTypes.bool,
    anyTouched: PropTypes.bool,
    isActive: PropTypes.bool,
    formValues: PropTypes.any,
    fieldsErrors: PropTypes.any,
  };

  constructor(props) {
    super(props);

    this.handleClone = this.handleClone.bind(this);
    this.markAsActive = this.markAsActive.bind(this);
    this.handleCancelDelete = this.handleCancelDelete.bind(this);
    this.handleConfirmDelete = this.handleConfirmDelete.bind(this);


    this.state = {
      isActive: false
    };
  }

  // shouldComponentUpdate(nextProps, nextState) {
  //   return this.props.isActive !== nextProps.isActive || !(_.isEqual(this.props.fieldsErrors, nextProps.fieldsErrors)) || !(_.isEqual(this.props.formValues, nextProps.formValues)) || !(_.isEqual(this.state, nextState)) || !(_.isEqual(this.props.metadata, nextProps.metadata));
  // }

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
        color: '#007dc4' // primary
      };
    }

    if (type === EVENT_TYPES.WARNING) {
      return {
        color: '#ed9d00' // warning
      };
    }

    if (type === EVENT_TYPES.CRITICAL) {
      return {
        color: '#d3435c' // critical
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
    this.props.onDelete(this.props.field.get('id'));
  }

  handleClone() {
    this.props.onClone(this.props.field.get('id'));
  }

  toolsPopconfirmDeleteButton() {
    return (
      <Popconfirm title="Are you sure?" overlayClassName="danger"
                  onConfirm={this.handleConfirmDelete}
                  onCancel={this.handleCancelDelete}
                  okText="Yes, Delete"
                  cancelText="Cancel">
        <Button icon="delete" size="small"
                onClick={this.markAsActive}
        />
      </Popconfirm>
    );
  }

  toolsDeleteButton() {
    return (
      <Button size="small" icon="delete"
              onClick={this.handleConfirmDelete}
      />
    );
  }

  toolsCloneButton() {
    return (
      <Button icon="copy" size="small"
              onClick={this.handleClone}/>
    );
  }

  toolsDragAndDropButton = SortableHandle(() => <Icon type="bars" className="cursor-move"/>);

  render() {
    const itemClasses = classnames({
      'product-metadata-item': true,
      'product-metadata-item-active': this.props.isActive || this.state.isActive,
    });

    return (
      <Scroll.Element name={this.props.field.get('name')}>
        <div className={itemClasses}>
          <Timeline>
            <Timeline.Item {...this.getPropsByType(this.props.type)}>
              <Row gutter={8}>
                <Col span={13}>
                  {this.getChildrenByType(Content.displayName)}
                  <Notifications onFocus={this.markAsActive} onBlur={this.handleCancelDelete}
                                 contactMetaFields={this.props.contactMetaFields} field={this.props.field}/>
                </Col>
                <Col span={9} offset={1}>
                  {this.getChildrenByType(Preview.displayName)}
                </Col>
                {this.props.tools && (
                  <Col span={1} className="product-events-event-tools">
                    <this.toolsDragAndDropButton/>

                    {!isEventPristine(this.props.field) && this.toolsPopconfirmDeleteButton() || this.toolsDeleteButton()}

                    {this.toolsCloneButton()}
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
      </Scroll.Element>
    );
  }

}

Base.Static = Static;
Base.Preview = Preview;
Base.Content = Content;

export default Base;
