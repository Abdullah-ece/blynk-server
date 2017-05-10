import React from 'react';
import {Base, Online, Offline, Event} from 'scenes/Products/components/Events';

class Events extends React.Component {

  render() {
    return (
      <div className="product-events-list">
        <Online/>
        <Offline/>
        <Event/>
        <Base type="warning"/>
        <Base type="alert"/>
      </div>
    );
  }

}

export default Events;
