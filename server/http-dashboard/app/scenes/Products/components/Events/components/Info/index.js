import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products'

class Info extends React.Component {

  render() {
    return (
      <Event type={EVENT_TYPES.INFO}/>
    );
  }

}

export default Info;
