import React from 'react';
// import {BackTop} from 'components';
import {Online, Offline, Info, Warning, Critical} from 'scenes/Products/components/Events';
import {EVENT_TYPES} from 'services/Products';

class Events extends React.Component {

  static propTypes = {
    fields: React.PropTypes.array,
  };

  getFieldsForTypes(fields, types) {
    const elements = [];

    const filterByTypes = (field) => types.indexOf(field.type) !== -1;

    if (fields && Array.isArray(fields)) {

      fields.filter(filterByTypes).forEach((field) => {

        let options = {
          key: `event${field.name}${field.type}`,
          fields: {
            name: field.name,
            isNotificationsEnabled: field.isNotificationsEnabled,
            pushNotifications: field.pushNotifications,
            emailNotifications: field.emailNotifications
          }
        };

        if (field.type === EVENT_TYPES.ONLINE) {
          elements.push(
            <Online.Static {...options}/>
          );
        }

        if (field.type === EVENT_TYPES.OFFLINE) {

          options = {
            ...options,
            fields: {
              ...options.fields,
              ignorePeriod: field.ignorePeriod
            }
          };

          elements.push(
            <Offline.Static {...options}/>
          );
        }

        if (field.type === EVENT_TYPES.INFO) {

          options = {
            ...options,
            fields: {
              ...options.fields,
              eventCode: field.eventCode,
              description: field.description
            }
          };

          elements.push(
            <Info.Static {...options}/>
          );
        }

        if (field.type === EVENT_TYPES.WARNING) {

          options = {
            ...options,
            fields: {
              ...options.fields,
              eventCode: field.eventCode,
              description: field.description
            }
          };

          elements.push(
            <Warning.Static {...options}/>
          );
        }

        if (field.type === EVENT_TYPES.CRITICAL) {

          options = {
            ...options,
            fields: {
              ...options.fields,
              eventCode: field.eventCode,
              description: field.description
            }
          };

          elements.push(
            <Critical.Static {...options}/>
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
        {/*<BackTop/>*/}
      </div>
    );
  }

}

export default Events;
