import React from 'react';
import AddNewMetadataField from "../../components/AddNewMetadataField/index";
import {Metadata as MetadataService} from 'services/Products';
import Metadata from "../../../../components/Metadata/index";
const MetadataFields = Metadata.Fields;
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as ProductAction from 'data/Products/actions';

@connect((state) => ({
  MetadataFields: state.Products.creating.metadata.fields
}), (dispatch) => ({
  addMetadataField: bindActionCreators(ProductAction.ProductMetadataFieldAdd, dispatch),
  deleteMetadataField: bindActionCreators(ProductAction.ProductMetadataFieldDelete, dispatch),
  updateMetadataFieldValues: bindActionCreators(ProductAction.ProductMetadataFieldValuesUpdate, dispatch)
}))
class ProductMetadata extends React.Component {

  static propTypes = {
    MetadataFields: React.PropTypes.array,
    addMetadataField: React.PropTypes.func,
    deleteMetadataField: React.PropTypes.func,
    updateMetadataFieldValues: React.PropTypes.func,
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

  getFields() {
    const fields = [];
    this.props.MetadataFields.forEach((field) => {

      const props = {
        id: field.id,
        key: field.id,
        form: `metadatafield${field.id}`,
        onDelete: this.handleDeleteField.bind(this),
        onChange: this.handleChangeField.bind(this)
      };

      if (field.type === MetadataService.Fields.TEXT) {
        fields.push(
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

    return fields;
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

  render() {

    const fields = this.getFields();

    return (
      <div>
        <Metadata.ItemsList>
          { fields }
        </Metadata.ItemsList>
        <AddNewMetadataField onFieldAdd={this.addMetadataField.bind(this)}/>
      </div>
    );
  }
}

export default ProductMetadata;
