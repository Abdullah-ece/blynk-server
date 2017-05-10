import React from 'react';
import Event from './../Event';
import {EVENT_TYPES} from 'services/Products';

class Critical extends React.Component {

  render() {
    return (
      <Event type={EVENT_TYPES.CRITICAL}/>
    );
  }

}

export default Critical;
