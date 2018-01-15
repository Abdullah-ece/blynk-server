import React from 'react';
import {FieldArray} from 'redux-form';
import {List} from './components';

class ProductMetadata extends React.Component {

  render() {

    return (
      <FieldArray name={`metaFields`} component={List}/>
    );
  }
}

export default ProductMetadata;
