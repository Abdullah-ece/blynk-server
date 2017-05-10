import React from 'react';
import {Online, Offline, Info, Warning, Critical} from 'scenes/Products/components/Events';

class Events extends React.Component {

  render() {
    return (
      <div className="product-events-list">
        <Online/>
        <Offline/>
        <Info/>
        <Warning/>
        <Critical/>
      </div>
    );
  }

}

export default Events;
