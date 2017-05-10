import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products';

class Warning extends React.Component {

  render() {
    return (
      <Event type={EVENT_TYPES.WARNING} form={this.props.form}/>
    );
  }

}

export default Warning;
