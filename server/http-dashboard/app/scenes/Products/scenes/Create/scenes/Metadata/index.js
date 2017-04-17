import React from 'react';
import AddNewMetadataField from "../../components/AddNewMetadataField/index";
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

  addMetadataField(type) {
    const id = this.genMetadataId();
    this.setState({
      metadata: this.state.metadata[id] = {
        id,
        type: type
      }
    });
  }

  render() {
    return (
      <div>
        <Metadata.ItemsList>
          <MetadataFields.TextField onChange={this.handleItemChange.bind(this)} id={1}/>
          <MetadataFields.TextField onChange={this.handleItemChange.bind(this)} id={2}/>
          <MetadataFields.TextField onChange={this.handleItemChange.bind(this)} id={3}/>
        </Metadata.ItemsList>
        <AddNewMetadataField/>
      </div>
    );
  }
}

export default ProductMetadata;
