import React from 'react';
import {
  Metadata,
  hardcodedRequiredMetadataFieldsNames
} from 'services/Products';
import {Switch, Select} from 'antd';
import {Field} from 'redux-form';
import FormItem from 'components/FormItem';
import {Item} from 'components/UI';
import {SimpleMatch} from 'services/Filters';
import _ from 'lodash';
import Static from './notifications-static';

class Notifications extends React.Component {

  static propTypes = {
    metadata: React.PropTypes.any,
    fields: React.PropTypes.object,
    onFocus: React.PropTypes.func,
    onBlur: React.PropTypes.func,
  };

  constructor(props) {
    super(props);


  }

  shouldComponentUpdate(nextProps, nextState) {
    return !(_.isEqual(this.props.fields, nextProps.fields)) || !(_.isEqual(this.state, nextState)) || !(_.isEqual(this.props.metadata, nextProps.metadata));
  }

  notificationSelect(props) {

    return (
      <Select mode="multiple"
              onFocus={props.input.onFocus}
              onBlur={props.input.onBlur}
              onChange={props.input.onChange}
              value={props.input.value || []}
              style={{width: '100%'}}
              placeholder="Select contact"
              allowClear={true}
              filterOption={(value, option) => SimpleMatch(value, option.key)}
              notFoundContent={!props.options.length ? 'Add a "Contact" type Metadata to enable notifications' : 'No field matches your request'}>
        { props.options }
      </Select>
    );
  }

  getMetadataContactFieldsWithEmail() {
    return this.props.metadata.filter((field) => {
      return field.type === Metadata.Fields.CONTACT && field.values && field.values.isEmailEnabled;
    }).map((field) => (
      <Select.Option key={field.values.name} value={(field.id).toString()}>{field.values.name}</Select.Option>
    ));
  }

  getDeviceOwnerField() {
    return this.props.metadata.filter((field) => {
      return field.values.name === hardcodedRequiredMetadataFieldsNames.DeviceOwner;
    }).map((field) => (
      <Select.Option key={field.values.name} value={(field.id).toString()}>{field.values.name}</Select.Option>
    ));
  }

  switcher(props) {
    return <Switch size="small" onChange={props.input.onChange} checked={!!props.input.value}/>;
  }

  render() {
    let notificationAvailableMetadataContactFields = this.getMetadataContactFieldsWithEmail().concat(
      this.getDeviceOwnerField()
    );

    return (
      <FormItem>
        <Item offset="small">
          <Field name="isNotificationsEnabled" component={this.switcher}/>
          { this.props.fields.isNotificationsEnabled && (
            <span className="product-events-notifications-label">Notifications On</span>
          ) || (
            <span className="product-events-notifications-label">Notifications Off</span>
          )}
        </Item>
        <FormItem visible={!!this.props.fields && !!this.props.fields.isNotificationsEnabled}>
          <Item label="E-mail to" offset="normal">
            <Field name="emailNotifications"
                   component={this.notificationSelect}
                   onFocus={this.props.onFocus} onBlur={this.props.onBlur}
                   options={notificationAvailableMetadataContactFields}/>
          </Item>
          <Item label="PUSH Notifications to">
            <Field name="pushNotifications"
                   component={this.notificationSelect}
                   onFocus={this.props.onFocus} onBlur={this.props.onBlur}
                   options={notificationAvailableMetadataContactFields}/>
          </Item>
        </FormItem>
      </FormItem>
    );
  }

}

Notifications.Static = Static;
export default Notifications;
