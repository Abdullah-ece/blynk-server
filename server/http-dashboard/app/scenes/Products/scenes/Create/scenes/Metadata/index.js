import React from 'react';
import AddNewMetadataField from "../../components/AddNewMetadataField/index";
import {Metadata as MetadataService} from 'services/Products';
import Metadata from "../../../../components/Metadata/index";
const MetadataFields = Metadata.Fields;

class ProductMetadata extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      metadataIntroVisible: null,
      metadata: {}
    };
  }

  handleItemChange(key, values) {
    console.log(key, values);
  }

  genMetadataId() {
    if (!Object.keys(this.state.metadata).length) return 1;

    let max = Math.max.apply({}, Object.keys(this.state.metadata));
    if (!isNaN(max))
      return ++max;

    return 1;
  }

  addMetadataField(params) {
    const id = this.genMetadataId();

    const metadata = Object.assign({}, this.state.metadata, {
      [id]: {
        id,
        type: params.type
      }
    });

    this.setState({
      metadata: metadata
    });
  }

  handleDelete(id) {

    let metadata = Object.assign({}, this.state.metadata);

    delete metadata[id];

    this.setState({metadata: metadata});
  }

  generateFields() {
    const fields = [];
    Object.keys(this.state.metadata).forEach((id) => {
      const field = this.state.metadata[id];

      const props = {
        onChange: this.handleItemChange.bind(this),
        key: id,
        onDelete: this.handleDelete.bind(this),
        id: Number(id)
      };

      if (field.type === MetadataService.Fields.TEXT) {
        fields.push(<MetadataFields.TextField {...props}/>);
      }
    });

    return fields;
  }

  render() {

    const fields = this.generateFields();

    return (
      <div>
        { !!fields.length && <Metadata.ItemsList>
          { fields }
          {/*<MetadataFields.TextField onChange={this.handleItemChange.bind(this)} id={1}/>*/}
          {/*<MetadataFields.TextField onChange={this.handleItemChange.bind(this)} id={2}/>*/}
          {/*<MetadataFields.TextField onChange={this.handleItemChange.bind(this)} id={3}/>*/}
        </Metadata.ItemsList> }
        <AddNewMetadataField onFieldAdd={this.addMetadataField.bind(this)}/>
      </div>
    );
  }
}

export default ProductMetadata;
