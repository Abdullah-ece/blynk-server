import React from 'react';
import {Online, Offline, /*Info, Warning, Critical*/} from 'scenes/Products/components/Events';
import {EVENT_TYPES} from 'services/Products';

class Events extends React.Component {

  static propTypes = {
    fields: React.PropTypes.array
  };

  getStaticFields(fields) {

    const elements = [];

    if (fields && Array.isArray(fields)) {
      fields.forEach((field, key) => {

        console.log(field);

        if (field.type === EVENT_TYPES.ONLINE) {
          elements.push(
            <Online key={key}/>
          );
        }

        if (field.type === EVENT_TYPES.OFFLINE) {
          elements.push(
            <Offline key={key}/>
          );
        }

      });
    }

    return elements;
  }

  render() {

    const staticFields = this.getStaticFields(this.props.fields);

    return (
      <div className="product-events-list">
        { staticFields }
      </div>
    );
  }

}

export default Events;
