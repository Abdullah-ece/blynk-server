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

        let options = {
          form: `event${field.id}`,
          initialValues: {
            name: field.values.name,
            isNotificationsEnabled: field.values.isNotificationsEnabled
          }
        };

        if (field.type === EVENT_TYPES.ONLINE) {
          elements.push(
            <Online key={key} {...options}/>
          );
        }

        if (field.type === EVENT_TYPES.OFFLINE) {

          options = {
            ...options,
            initialValues: {
              ...options.initialValues,
              ignorePeriod: field.values.ignorePeriod
            }
          };

          elements.push(
            <Offline key={key} {...options}/>
          );
        }

        if (field.type === EVENT_TYPES.INFO) {

          options = {
            ...options,
            initialValues: {
              ...options.initialValues,
              eventCode: field.values.eventCode,
              description: field.values.description
            }
          };

          elements.push(
            <Info key={key} {...options}/>
          );
        }

        if (field.type === EVENT_TYPES.WARNING) {

          options = {
            ...options,
            initialValues: {
              ...options.initialValues,
              eventCode: field.values.eventCode,
              description: field.values.description
            }
          };

          elements.push(
            <Warning key={key} {...options}/>
          );
        }

        if (field.type === EVENT_TYPES.CRITICAL) {

          options = {
            ...options,
            initialValues: {
              ...options.initialValues,
              eventCode: field.values.eventCode,
              description: field.values.description
            }
          };

          elements.push(
            <Critical key={key} {...options}/>
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
