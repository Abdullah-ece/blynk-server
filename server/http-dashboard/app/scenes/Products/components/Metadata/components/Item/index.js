import React from 'react';
import {Row, Col, Icon, Popconfirm, Button} from 'antd';
import FormItem from 'components/FormItem';
import Preview from '../../components/Preview';
import {SortableHandle} from 'react-sortable-hoc';
import {MetadataSelect} from 'components/Form';
import {MetadataRoles} from 'services/Roles';
import classnames from 'classnames';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {reduxForm, touch, Form} from 'redux-form';
import {ProductMetadataFieldInvalidFlagUpdate} from 'data/Product/actions';
const DragHandler = SortableHandle(() => <Icon type="bars" className="cursor-move"/>);
import Static from './static';

@connect(() => ({}), (dispatch) => ({
  touchFormById: bindActionCreators(touch, dispatch),
  updateMetadataFieldInvalidFlag: bindActionCreators(ProductMetadataFieldInvalidFlagUpdate, dispatch)
}))
@reduxForm({
  touchOnChange: true
})
class MetadataItem extends React.Component {

  static propTypes = {
    anyTouched: React.PropTypes.bool,
    invalid: React.PropTypes.bool,
    preview: React.PropTypes.object,
    form: React.PropTypes.string,
    fields: React.PropTypes.object,
    children: React.PropTypes.any,
    onDelete: React.PropTypes.func,
    touchFormById: React.PropTypes.func,
    onClone: React.PropTypes.func,
    touched: React.PropTypes.bool,
    updateMetadataFieldInvalidFlag: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.invalid = false;

    this.state = {
      isActive: false
    };
  }

  componentWillReceiveProps(props) {
    if (this.invalid !== props.invalid) {
      this.props.updateMetadataFieldInvalidFlag({
        id: props.id,
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

  handleSubmit() {
    this.props.touchFormById(this.props.form, ...Object.keys(this.props.fields));
  }

  render() {

    let deleteButton;
    if (this.props.anyTouched) {
      deleteButton = (<Popconfirm title="Are you sure?" overlayClassName="danger"
                                  onConfirm={this.handleConfirmDelete.bind(this)}
                                  onCancel={this.handleCancelDelete.bind(this)} okText="Yes, Delete"
                                  cancelText="Cancel">
        <Button icon="delete" size="small" onClick={this.markAsActive.bind(this)}/>
      </Popconfirm>);
    } else {
      deleteButton = (<Button size="small" icon="delete" onClick={this.handleConfirmDelete.bind(this)}/>);
    }

    const itemClasses = classnames({
      'product-metadata-item': true,
      'product-metadata-item-active': this.state.isActive,
    });

    return (
      <div className={itemClasses}>
        <Form onSubmit={this.handleSubmit.bind(this)}>
          <Row gutter={8}>
            <Col span={12}>
              { this.props.children }
            </Col>
            <Col span={4}>
              <FormItem offset={false}>
                <FormItem.Title>Who can edit</FormItem.Title>
                <FormItem.Content>
                  <MetadataSelect name="role" style={{width: 120}} values={MetadataRoles}/>
                </FormItem.Content>
              </FormItem>
            </Col>
            <Col span={6} offset={1}>
              { this.preview() }
            </Col>
          </Row>
          <div className="product-metadata-item-tools">
            <DragHandler/>
            {deleteButton}
            <Button icon="copy" size="small" onClick={this.props.onClone.bind(this)}/>
          </div>
        </Form>
      </div>
    );
  }
}

MetadataItem.Static = Static;
export default MetadataItem;
