import React from 'react';
import {Base, Online} from 'scenes/Products/components/Events';

class Events extends React.Component {

  render() {
    return (
      <div className="product-events-list">
        <Online/>
        <Base type="offline"/>
        <Base type="info"/>
        <Base type="warning"/>
        <Base type="alert"/>
      </div>
    )
  }

}

export default Events;
