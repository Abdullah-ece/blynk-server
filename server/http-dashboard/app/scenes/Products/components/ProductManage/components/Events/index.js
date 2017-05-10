import React from 'react';
import {Online, Offline, Info, Warning, Critical} from 'scenes/Products/components/Events';
import {EVENT_TYPES} from 'services/Products';

class Events extends React.Component {

  static propTypes = {
    fields: React.PropTypes.array
  };

  getFieldsForTypes(fields, types) {
    const elements = [];

    const filterByTypes = (field) => types.indexOf(field.type) !== -1;

    if (fields && Array.isArray(fields)) {

      fields.filter(filterByTypes).forEach((field, key) => {

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

        if (field.type === EVENT_TYPES.INFO) {
          elements.push(
            <Info key={key}/>
          );
        }

        if (field.type === EVENT_TYPES.WARNING) {
          elements.push(
            <Warning key={key}/>
          );
        }

        if (field.type === EVENT_TYPES.CRITICAL) {
          elements.push(
            <Critical key={key}/>
          );
        }

      });
    }

    return elements;
  }

  getStaticFields(fields) {
    return this.getFieldsForTypes(fields, [EVENT_TYPES.ONLINE, EVENT_TYPES.OFFLINE]);
  }

  getDynamicFields(fields) {
    return this.getFieldsForTypes(fields, [EVENT_TYPES.INFO, EVENT_TYPES.WARNING, EVENT_TYPES.CRITICAL]);
  }

  render() {

    const staticFields = this.getStaticFields(this.props.fields);
    const dynamicFields = this.getDynamicFields(this.props.fields);

    return (
      <div className="product-events-list">
        { staticFields }
        { dynamicFields }
      </div>
    );
  }

}

export default Events;
