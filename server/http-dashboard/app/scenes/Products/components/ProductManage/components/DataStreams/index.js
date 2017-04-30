import React from 'react';
import {AddDataStreamsFields} from 'scenes/Products/components/AddField';
import {DataStreamsBaseField, DataStreamsItemsList} from "scenes/Products/components/DataStreams";

// import {SortableContainer, SortableElement, arrayMove} from 'react-sortable-hoc';

class DataStreams extends React.Component {

  addDataStreamsField() {
    //  some there
  }

  render() {
    return (
      <div>
        <DataStreamsItemsList>
          <DataStreamsBaseField form="datastream"/>
        </DataStreamsItemsList>
        <AddDataStreamsFields onFieldAdd={this.addDataStreamsField.bind(this)}/>
      </div>
    );
  }
}

export default DataStreams;
