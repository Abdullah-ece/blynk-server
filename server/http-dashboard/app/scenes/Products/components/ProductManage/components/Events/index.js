import React from 'react';
import {Online, Offline, Info, Warning, Critical} from 'scenes/Products/components/Events';
import {EVENT_TYPES} from 'services/Products';
import _ from 'lodash';

class Events extends React.Component {

  static propTypes = {
    fields: React.PropTypes.array,

    onFieldsChange: React.PropTypes.func
  };

  handleFieldChange(values, /*dispatch, props*/) {
    if (values.id) {

      let fields = [...this.props.fields];

      const fieldIndex = _.findIndex(this.props.fields, {id: values.id});

      fields[fieldIndex] = {
        ...fields[fieldIndex],
        values: {
          ...fields[fieldIndex].values,
          ...values
        }
      };

      this.props.onFieldsChange(
        fields
      );

    } else {
      throw Error('Missing id parameter for handleFieldChange');
    }
  }

  handleFieldDelete(values) {
    if (values.id) {

      let fields = this.props.fields.remove(
        (field) => Number(field.id) === Number(values.id) || Number(field.values.id) === Number(values.id)
      );

      this.props.onFieldsChange(fields);

    } else {
      throw Error('Missing id parameter for handleFieldDelete');
    }
  }

  getFieldsForTypes(fields, types) {
    const elements = [];

    const filterByTypes = (field) => types.indexOf(field.type) !== -1;

    if (fields && Array.isArray(fields)) {

      fields.filter(filterByTypes).forEach((field, key) => {

        let options = {
          form: `event${field.id}`,
          initialValues: {
            id: field.id,
            name: field.values.name,
            isNotificationsEnabled: field.values.isNotificationsEnabled,
            emailNotifications: field.values.emailNotifications && field.values.emailNotifications.map((value) => value.toString()),
            pushNotifications: field.values.pushNotifications && field.values.pushNotifications.map((value) => value.toString()),
          },
          onChange: this.handleFieldChange.bind(this),
          onDelete: this.handleFieldDelete.bind(this)
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
