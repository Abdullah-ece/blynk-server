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
  addMetadataField: bindActionCreators(ProductAction.ProductMetadataFieldAdd, dispatch)
}))
class ProductMetadata extends React.Component {

  static propTypes = {
    MetadataFields: React.PropTypes.array,
    addMetadataField: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    props.addMetadataField({
      type: MetadataService.Fields.TEXT,
      values: {
        name: 'Series',
        value: ''
      }
    });

    props.addMetadataField({
      type: MetadataService.Fields.TEXT,
      values: {
        name: 'Manufactured By',
        value: 'Apple'
      }
    });
  }

  getFields() {
    const fields = [];
    this.props.MetadataFields.forEach((field, key) => {

      const props = {
        id: key,
        key: key,
        form: `metadatafield${key}`
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

  render() {

    const fields = this.getFields();

    return (
      <div>
        <Metadata.ItemsList>
          { fields }
          {/*<MetadataFields.TextField form="form1"/>*/}
          {/*<MetadataFields.TextField form="form2"/>*/}
          {/*<MetadataFields.TextField form="form3"/>*/}
          {/*<MetadataFields.TextField onChange={this.handleItemChange.bind(this)} id={1}/>*/}
          {/*<MetadataFields.TextField onChange={this.handleItemChange.bind(this)} id={2}/>*/}
          {/*<MetadataFields.TextField onChange={this.handleItemChange.bind(this)} id={3}/>*/}
        </Metadata.ItemsList>
        <AddNewMetadataField onFieldAdd={this.addMetadataField.bind(this)}/>
      </div>
    );
  }
}

export default ProductMetadata;
