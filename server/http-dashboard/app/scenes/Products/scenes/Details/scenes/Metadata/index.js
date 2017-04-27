import React from 'react';
import MetadataComponents from 'scenes/Products/components/Metadata';
const {ItemsList, Fields: {TextField}} = MetadataComponents;

class Metadata extends React.Component {
  render() {
    return (
      <ItemsList static>
        <TextField.Static data={{name: 'Field', value: 'value'}}/>
      </ItemsList>
    );
  }
}

export default Metadata;
