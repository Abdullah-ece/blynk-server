import React from 'react';
import {List} from './components';
import {FieldArray} from 'redux-form';

class Events extends React.Component {

  render() {
    return (
      <FieldArray component={List} name={`events`}/>
    );
  }

}

export default Events;
