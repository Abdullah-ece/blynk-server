import React from 'react';
import AddNewMetadataField from "../../components/AddNewMetadataField/index";
import {Metadata as MetadataService} from 'services/Products';
import Metadata from "../../../../components/Metadata/index";
const MetadataFields = Metadata.Fields;
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as ProductAction from 'data/Products/actions';
import _ from 'lodash';
import {SortableContainer, SortableElement, arrayMove} from 'react-sortable-hoc';

@connect((state) => ({
  MetadataFields: state.Products.creating.metadata.fields
}), (dispatch) => ({
  addMetadataField: bindActionCreators(ProductAction.ProductMetadataFieldAdd, dispatch),
  deleteMetadataField: bindActionCreators(ProductAction.ProductMetadataFieldDelete, dispatch),
  updateMetadataFieldValues: bindActionCreators(ProductAction.ProductMetadataFieldValuesUpdate, dispatch),
  updateMetadataFieldsOrder: bindActionCreators(ProductAction.ProductMetadataFieldsOrderUpdate, dispatch)
}))
class ProductMetadata extends React.Component {

  static propTypes = {
    MetadataFields: React.PropTypes.array,
    addMetadataField: React.PropTypes.func,
    deleteMetadataField: React.PropTypes.func,
    updateMetadataFieldValues: React.PropTypes.func,
    updateMetadataFieldsOrder: React.PropTypes.func,
  };

  constructor(props) {
    super(props);
  }

  handleChangeField(values, dispatch, props) {
    this.props.updateMetadataFieldValues({
      id: props.id,
      values: values
    });
  }

  SortableItem = SortableElement(({value}) => {

    const field = value;

    const props = {
      id: field.id,
      key: field.id,
      form: `metadatafield${field.id}`,
      onChange: this.handleChangeField.bind(this),
      validate: this.metadataFieldValidation.bind(this),
      onDelete: this.handleDeleteField.bind(this),
      onClone: this.handleCloneField.bind(this)
    };

    if (field.type === MetadataService.Fields.TEXT) {
      return (
        <MetadataFields.TextField
          {...props}
          initialValues={{
            name: field.values.name,
            value: field.values.value
          }}
        />
      );
    }
  });

  SortableList = SortableContainer(({items}) => {
    return (
      <Metadata.ItemsList>
        {items.map((value, index) => {
          return (
            <this.SortableItem key={`item-${value.id}`} index={index} value={value}/>
          );
        })}
      </Metadata.ItemsList>
    );
  });

  metadataFieldValidation(values, props) {
    const errors = {};

    this.props.MetadataFields.forEach((field) => {
      if (field.values.name === values.name && Number(props.id) !== Number(field.id)) {
        errors.name = 'Name should be unique';
      }
    });

    return errors;
  }

  handleCloneField(id) {

    const cloned = _.find(this.props.MetadataFields, {id: id});

    this.props.addMetadataField({
      ...cloned,
      values: {
        ...cloned.values,
        name: `${cloned.values.name} Copy`
      }
    });
  }

  addMetadataField(params) {
    this.props.addMetadataField({
      type: params.type,
      values: {
        name: '',
        value: ''
      }
    });
  }

  handleDeleteField(key) {
    this.props.deleteMetadataField({
      id: key
    });
  }

  onSortEnd({oldIndex, newIndex}) {

    this.props.updateMetadataFieldsOrder(
      arrayMove(this.props.MetadataFields, oldIndex, newIndex)
    );

  }

  render() {

    return (
      <div>
        { this.props.MetadataFields && this.props.MetadataFields.length && (
          <this.SortableList items={this.props.MetadataFields} onSortEnd={this.onSortEnd.bind(this)}
                             useDragHandle={true}
                             lockAxis="y"
                             helperClass="product-metadata-item-drag-active"/>) || null
        }

        <AddNewMetadataField onFieldAdd={this.addMetadataField.bind(this)}/>
      </div>
    );
  }
}

export default ProductMetadata;
