import React from 'react';
import {FieldArray} from 'redux-form';
import {List} from './components';

class DataStreams extends React.Component {

  render() {

    return (
      <FieldArray name={`dataStreams`} component={List}/>
    );
  }
}

export default DataStreams;
