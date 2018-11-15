import React from 'react';
import Scroll from 'react-scroll';
import classnames from 'classnames';
import {Button, Col, Icon, Popconfirm, Row} from 'antd';
import {SortableHandle} from 'react-sortable-hoc';
import {MetadataSelect as MetadataFormSelect} from 'components/Form';
import Preview from 'scenes/Products/components/Preview';
import FormItem from 'components/FormItem';
import Static from './static';
import Validation from 'services/Validation';
import PropTypes from 'prop-types';
const DragHandler = SortableHandle(() => <Icon type="bars" className="cursor-move"/>);

class DataStreamItem extends React.Component {
  static propTypes = {
    preview: PropTypes.object,
    field: PropTypes.object,
    fieldSyncErrors: PropTypes.object,

    name: PropTypes.string,
    isDirty: PropTypes.bool,

    children: PropTypes.any,
    onDelete: PropTypes.func,
    onClone: PropTypes.func,
    onPinChange: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.markAsActive = this.markAsActive.bind(this);
    this.handleCancelDelete = this.handleCancelDelete.bind(this);
    this.handleConfirmDelete = this.handleConfirmDelete.bind(this);
    this.handlePinChange = this.handlePinChange.bind(this);

    this.state = {
      isActive: false
    };
  }

  handleConfirmDelete() {
    if (this.props.onDelete)
      this.props.onDelete();
  }

  handleCancelDelete() {
    this.setState({isActive: false});
  }

  markAsActive() {
    this.setState({isActive: true});
  }

  handlePinChange(event, value) {
    this.props.onPinChange(value);
  }

  preview() {

    if (this.props.fieldSyncErrors && this.props.fieldSyncErrors.size && this.props.isDirty) {
      return (<Preview> <Preview.Unavailable /> </Preview>);
    }

    if(!this.props.preview.name)
      return null;

    return (
      <Preview>
        <Preview.Name>{this.props.preview.name}</Preview.Name>
        <Preview.Value>{this.props.preview.value || 'Empty'}</Preview.Value>
      </Preview>
    );

  }

  Pins() {
    const pins = [];
    [...Array(256).keys()].map((i, k) => {
      pins.push({
        key: `${k}`,
        value: `V${k}`
      });
    });
    return pins;
  }

  render() {

    let deleteButton;
    if (this.props.isDirty) {
      deleteButton = (
        <Popconfirm title="Are you sure?" overlayClassName="danger"
                    onConfirm={this.handleConfirmDelete}
                    onCancel={this.handleCancelDelete} okText="Yes, Delete"
                    cancelText="Cancel">
          <Button icon="delete" size="small" onClick={this.markAsActive}/>
        </Popconfirm>
      );
    } else {
      deleteButton = (
        <Button size="small" icon="delete" onClick={this.handleConfirmDelete}/>
      );
    }

    const itemClasses = classnames({
      'product-metadata-item': true,
      'product-metadata-item-active': this.state.isActive,
    });

    return (
      <Scroll.Element name={this.props.field.get('name')}>
        <div className={itemClasses}>
          <Row gutter={8}>
            <Col span={13}>
              {this.props.children}
            </Col>
            <Col span={3}>

              <FormItem.TitleGroup>
                <FormItem.Title>Pin</FormItem.Title>
              </FormItem.TitleGroup>
              <FormItem.Content>
                <MetadataFormSelect name={`${this.props.name}.pin`}
                                    onFocus={this.markAsActive}
                                    onBlur={this.handleCancelDelete}
                                    onChange={this.handlePinChange}
                                    type="text"
                                    placeholder="Pin"
                                    dropdownClassName="product-metadata-item-unit-dropdown"
                                    values={this.Pins()}
                                    validate={[
                                      Validation.Rules.required
                                    ]}/>
              </FormItem.Content>
            </Col>
            <Col span={8}>
              {this.preview()}
            </Col>
          </Row>
          <div className="product-metadata-item-tools">
            <DragHandler/>
            {deleteButton}
            <Button icon="copy" size="small" onClick={this.props.onClone}/>
          </div>
        </div>
      </Scroll.Element>
    );
  }
}

DataStreamItem.Static = Static;

export default DataStreamItem;
