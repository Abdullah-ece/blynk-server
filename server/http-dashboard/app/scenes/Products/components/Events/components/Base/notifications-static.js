import React from 'react';
import FormItem from 'components/FormItem';
import {Item} from 'components/UI';
import Static from './notifications-static';

class Notifications extends React.Component {

  static propTypes = {
    metadata: React.PropTypes.any,
    fields: React.PropTypes.object
  };

  getEmailNotifications() {
    if (!Array.isArray(this.props.fields.emailNotifications)) return [];
    return this.props.fields.emailNotifications.map((field, key) => {
      return <div key={key} className={`product-metadata-static-field-inline`}>{field.value}</div>;
    });
  }

  getPushNotifications() {
    if (!Array.isArray(this.props.fields.pushNotifications)) return [];
    return this.props.fields.pushNotifications.map((field, key) => {
      return <div key={key} className={`product-metadata-static-field-inline`}>{field.value}</div>;
    });
  }

  getSmsNotifications() {
    if (!Array.isArray(this.props.fields.smsNotifications)) return [];
    return this.props.fields.smsNotifications.map((field, key) => {
      return <div key={key} className={`product-metadata-static-field-inline`}>{field.value}</div>;
    });
  }

  render() {

    const emailNotifications = this.getEmailNotifications();
    const pushNotifications = this.getPushNotifications();
    const smsNotifications = this.getSmsNotifications();

    return (
      <FormItem>
        <FormItem visible={!!this.props.fields && !!this.props.fields.isNotificationsEnabled}>
          {!!emailNotifications.length && (
            <Item label="E-mail to" offset="extra-small">
              {emailNotifications}
            </Item>
          )}
          {!!pushNotifications.length && (
            <Item label="PUSH notifications to">
              {pushNotifications}
            </Item>
          )}
          {!!smsNotifications.length && (
            <Item label="SMS notifications to">
              {pushNotifications}
            </Item>
          )}
        </FormItem>
      </FormItem>
    );
  }

}

Notifications.Static = Static;
export default Notifications;
