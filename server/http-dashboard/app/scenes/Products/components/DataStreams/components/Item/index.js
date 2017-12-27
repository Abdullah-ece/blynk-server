import React from 'react';
import Scroll from 'react-scroll';
import classnames from 'classnames';
import {Button, Col, Icon, Popconfirm, Row} from 'antd';
import {SortableHandle} from 'react-sortable-hoc';
import {MetadataSelect as MetadataFormSelect} from 'components/Form';
import {Form, reduxForm, touch} from 'redux-form';
import Preview from 'scenes/Products/components/Preview';
import FormItem from 'components/FormItem';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import Static from './static';
import Validation from 'services/Validation';

const DragHandler = SortableHandle(() => <Icon type="bars" className="cursor-move"/>);

@connect(() => ({}), (dispatch) => ({
  touchFormById: bindActionCreators(touch, dispatch)
}))
@reduxForm({
  shouldValidate: () => true, // dirty hack
  touchOnChange: true
})
class DataStreamItem extends React.Component {
  static propTypes = {
    anyTouched: React.PropTypes.bool,
    invalid: React.PropTypes.bool,
    preview: React.PropTypes.object,
    field: React.PropTypes.object,
    form: React.PropTypes.string,
    fields: React.PropTypes.object,
    children: React.PropTypes.any,
    onDelete: React.PropTypes.func,
    touchFormById: React.PropTypes.func,
    onClone: React.PropTypes.func,
    onChange: React.PropTypes.func,
    touched: React.PropTypes.bool,
    isActive: React.PropTypes.bool,
    updateMetadataFieldInvalidFlag: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.invalid = false;

    this.handleSubmit = this.handleSubmit.bind(this);
    this.markAsActive = this.markAsActive.bind(this);
    this.handleCancelDelete = this.handleCancelDelete.bind(this);
    this.handleConfirmDelete = this.handleConfirmDelete.bind(this);

    this.state = {
      isActive: false
    };
  }

  componentWillReceiveProps(props) {
    if (this.invalid !== props.invalid) {
      this.props.onChange({
        ...props.field,
        invalid: props.invalid
      });
      this.invalid = props.invalid;
    }
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

  handleSubmit() {
    this.props.touchFormById(this.props.form, ...Object.keys(this.props.fields));
  }

  preview() {

    if (!this.props.anyTouched && !this.props.preview.name) {
      return null;
    }

    if (this.props.invalid) {
      return (<Preview> <Preview.Unavailable /> </Preview>);
    }

    return (
      <Preview>
        <Preview.Name>{this.props.preview.name}</Preview.Name>
        <Preview.Value>{this.props.preview.value || 'Empty'}</Preview.Value>
      </Preview>
    );

  }

  Pins() {
    const pins = [];
    [...Array(128).keys()].map((i, k) => {
      pins.push({
        key: `${k}`,
        value: `V${k}`
      });
    });
    return pins;
  }

  render() {

    let deleteButton;
    if (this.props.anyTouched) {
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
      'product-metadata-item-active': this.props.isActive || this.state.isActive,
    });

    return (
      <Scroll.Element name={this.props.field.name}>
        <div className={itemClasses}>
          <Form onSubmit={this.handleSubmit}>
            <Row gutter={8}>
              <Col span={13}>
                {this.props.children}
              </Col>
              <Col span={3}>

                <FormItem.TitleGroup>
                  <FormItem.Title>Pin</FormItem.Title>
                </FormItem.TitleGroup>
                <FormItem.Content>
                  <MetadataFormSelect name="pin"
                                      onFocus={this.markAsActive}
                                      onBlur={this.handleCancelDelete}
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
                { this.preview() }
              </Col>
            </Row>
            <div className="product-metadata-item-tools">
              <DragHandler/>
              {deleteButton}
              <Button icon="copy" size="small" onClick={this.props.onClone}/>
            </div>
          </Form>
        </div>
      </Scroll.Element>
    );
  }
}

DataStreamItem.Static = Static;

export default DataStreamItem;
