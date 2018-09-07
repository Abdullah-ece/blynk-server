import React from 'react';
import Scroll from 'react-scroll';
import {Row, Col, Icon, Popconfirm, Button} from 'antd';
import FormItem from 'components/FormItem';
import Preview from 'scenes/Products/components/Preview';
import IconSelect from './components/IconSelect';
import {SortableHandle} from 'react-sortable-hoc';
import {MetadataSelect} from 'components/Form';
import {MetadataRoles} from 'services/Roles';
import classnames from 'classnames';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {
  // reduxForm,
  touch,
  // Form,
  getFormSyncErrors} from 'redux-form';
const DragHandler = SortableHandle(() => <Icon type="bars" className="cursor-move"/>);
import Static from './static';
// import _ from 'lodash';
import {hardcodedRequiredMetadataFieldsNames, Metadata} from 'services/Products';
import PropTypes from 'prop-types';

@connect((state, ownProps) => ({
  events: state.Product.edit.events.fields,
  fieldsErrors: getFormSyncErrors(ownProps.form)(state)
}), (dispatch) => ({
  touchFormById: bindActionCreators(touch, dispatch)
}))
class MetadataItem extends React.PureComponent {

  static propTypes = {
    events: PropTypes.any,
    anyTouched: PropTypes.bool,
    isDirty: PropTypes.bool,
    invalid: PropTypes.bool,
    preview: PropTypes.object,
    fieldsErrors: PropTypes.any,
    index: PropTypes.any,
    form: PropTypes.string,
    fields: PropTypes.object,
    children: PropTypes.any,
    onDelete: PropTypes.func,
    touchFormById: PropTypes.func,
    onClone: PropTypes.func,
    id: PropTypes.number,
    onChange: PropTypes.func,
    field: PropTypes.object,
    touched: PropTypes.bool,
    isActive: PropTypes.bool,
    tools: PropTypes.bool,
    updateMetadataFieldInvalidFlag: PropTypes.func,
    metaFieldKey: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number
    ]),

    addBefore: PropTypes.any,
  };

  constructor(props) {
    super(props);

    this.invalid = false;

    this.state = {
      isActive: false
    };

    this.handleConfirmDelete = this.handleConfirmDelete.bind(this);
    this.handleCancelDelete = this.handleCancelDelete.bind(this);
    this.markAsActive = this.markAsActive.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);

  }

  componentWillMount() {
    if (this.props.field.values.isSavedBefore) {
      this.props.touchFormById(this.props.form, ...Object.keys(this.props.field.values));
    }
  }

  componentWillReceiveProps(props) {
    if (this.invalid !== props.invalid) {
      // this.props.onChange({
      //   ...props.field,
      //   invalid: props.invalid
      // });
      this.invalid = props.invalid;
    }
  }

  // shouldComponentUpdate(nextProps) {
  //   return (
  //     (nextProps.preview.name !== this.props.preview.name) ||
  //     (nextProps.preview.value !== this.props.preview.value) ||
  //     (nextProps.index !== this.props.index)
  //   );
  // }

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

    const name = this.props.preview.name && this.props.preview.name.trim();

    if (!this.props.anyTouched && !name) {
      return null;
    }

    if (this.props.invalid && !name) {
      return (<Preview> <Preview.Unavailable /> </Preview>);
    }

    return (
      <Preview inline={this.props.preview.inline}>
        <Preview.Name>{name}</Preview.Name>
        <Preview.Value>{this.props.preview.value || 'Empty'}</Preview.Value>
      </Preview>
    );

  }

  handleSubmit() {
    this.props.touchFormById(this.props.form, ...Object.keys(this.props.fields));
  }

  isItemLocation() {
    return this.props.field.get('type') === Metadata.Fields.LOCATION;
  }

  isItemEnabledLocation() {
    return this.props.field.get('type') === Metadata.Fields.LOCATION && this.props.field.get('isLocationEnabled');
  }

  isItemLocationWithDataFromDevice() {
    return this.props.field.get('type') === Metadata.Fields.LOCATION && this.props.field.get('isLocationGetFromDevice');
  }

  isContentVisible() {
    return this.isItemLocation() ? this.isItemEnabledLocation() && !this.isItemLocationWithDataFromDevice() : true;
  }

  isRoleSelectDisabled(fieldName) {
    switch(String(fieldName)){
      case hardcodedRequiredMetadataFieldsNames.Manufacturer :
        return true;

      default:
        return false;
    }
  }



  render() {
    let deleteButton;
    if (this.props.isDirty) {
      deleteButton = (<Popconfirm title="Are you sure?" overlayClassName="danger"
                                  onConfirm={this.handleConfirmDelete}
                                  onCancel={this.handleCancelDelete} okText="Yes, Delete"
                                  cancelText="Cancel">
        <Button icon="delete" size="small" onClick={this.markAsActive}/>
      </Popconfirm>);
    } else {
      deleteButton = (<Button size="small" icon="delete" onClick={this.handleConfirmDelete}/>);
    }

    const itemClasses = classnames({
      'product-metadata-item': true,
      'product-metadata-item-active': this.state.isActive,
    });

    const isRoleSelectDisabled = this.isRoleSelectDisabled(this.props.field.get("name"), this.props.field.get('type'));

    return (
      <Scroll.Element name={this.props.field.name}>
        <div className={itemClasses}>
          {this.props.addBefore && this.props.addBefore}
          {this.isContentVisible() ? (
            <Row gutter={0}>
              <Col span={2} style={{width: '48px'}}>
                <IconSelect name={`metaFields.${this.props.metaFieldKey}.selectedIcon`}/>
              </Col>
              <Col span={10}>
                {this.props.children}
              </Col>
              <Col span={3}>
                {isRoleSelectDisabled ? '' :
                  <FormItem offset={false}>

                    <FormItem.Title>Who can edit</FormItem.Title>
                    <FormItem.Content>
                      <MetadataSelect disabled={isRoleSelectDisabled}
                                      onFocus={this.markAsActive}
                                      onBlur={this.handleCancelDelete}
                                      name={`metaFields.${this.props.metaFieldKey}.role`}
                                      style={{width: '100%'}}
                                      values={MetadataRoles}
                      />
                    </FormItem.Content>
                  </FormItem>
                }
              </Col>
              <Col span={8}>
                {this.preview()}
              </Col>
            </Row>
          ) : (null)}
          {this.props.tools && (
            <div className="product-metadata-item-tools">
              <DragHandler/>
              {deleteButton}
              <Button icon="copy" size="small" onClick={this.props.onClone}/>
            </div>
          )}
        </div>
      </Scroll.Element>
    );
  }
}

MetadataItem.Static = Static;
export default MetadataItem;
