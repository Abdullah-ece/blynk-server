import React from 'react';

import {Title, Section, Item} from '../../index';
import {Select, message} from 'antd';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import Timezones from 'services/timeszones';
import Field from '../../components/Field';
import InviteUsersForm from './components/InviteUsersForm';
import OrganizationUsers from './components/OrganizationUsers';
import OrganizationBranding from './components/OrganizationBranding';
import {SubmissionError, reset} from 'redux-form';
import {
  OrganizationFetch,
  OrganizationUpdateName,
  OrganizationSave,
  OrganizationUpdateTimezone,
  OrganizationUsersFetch,
  OrganizationSendInvite
} from 'data/Organization/actions';

import './styles.less';

@connect((state) => ({
  Organization: state.Organization,
  Account: state.Account
}), (dispatch) => ({
  OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch),
  OrganizationUsersFetch: bindActionCreators(OrganizationUsersFetch, dispatch),
  OrganizationUpdateName: bindActionCreators(OrganizationUpdateName, dispatch),
  OrganizationSave: bindActionCreators(OrganizationSave, dispatch),
  OrganizationUpdateTimezone: bindActionCreators(OrganizationUpdateTimezone, dispatch),
  OrganizationSendInvite: bindActionCreators(OrganizationSendInvite, dispatch),
  ResetForm: bindActionCreators(reset, dispatch)
}))
class OrganizationSettings extends React.Component {

  static propTypes = {
    Organization: React.PropTypes.object,
    OrganizationFetch: React.PropTypes.func,
    OrganizationUsersFetch: React.PropTypes.func,
    OrganizationUpdateName: React.PropTypes.func,
    OrganizationSave: React.PropTypes.func,
    OrganizationUpdateTimezone: React.PropTypes.func,
    OrganizationSendInvite: React.PropTypes.func,
    ResetForm: React.PropTypes.func,
    Account: React.PropTypes.object
  };

  constructor(props) {
    super(props);
  }

  handleInviteSubmit(values) {
    return this.props.OrganizationSendInvite({
      id: this.props.Account.orgId,
      email: values.email,
      name: values.name,
      role: values.role
    }).then(() => {
      this.props.OrganizationUsersFetch({
        id: this.props.Account.orgId
      });
      this.props.ResetForm('OrganizationSettingsInviteUsersForm');
      this.showInviteSuccess();
    }).catch((err) => {
      this.showInviteError(
        err.error.response.message || 'Error sending invite'
      );
      new SubmissionError(err);
    });
  }


  handleTimezoneChange(timezone) {
    const hideUpdatingMessage = message.loading('Updating organization timezone...', 0);
    this.props.OrganizationSave(Object.assign({}, this.props.Organization, {tzName: timezone})).then(() => {
      this.props.OrganizationUpdateTimezone(timezone);
      hideUpdatingMessage();
    }).catch((err) => {
      hideUpdatingMessage();
      message.error(err && err.error && err.error.response.message);
    });
  }

  handleNameSave(name) {
    const hideUpdatingMessage = message.loading('Updating organization name..', 0);
    this.props.OrganizationSave(Object.assign({}, this.props.Organization, {name: name})).then(() => {
      this.props.OrganizationUpdateName(name);
      hideUpdatingMessage();
    }).catch((err) => {
      hideUpdatingMessage();
      message.error(err && err.error && err.error.response.message);
    });
  }

  generateOptions() {
    const options = [];
    for (let timezone in Timezones) {
      options.push(<Select.Option key={timezone}>{Timezones[timezone]}</Select.Option>);
    }
    return options;
  }

  timezoneSearch(input, option) {
    return option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  }

  render() {

    const timezonesOptions = this.generateOptions();

    return (
      <div className="user-profile">
        <Title text="Organization Settings"/>
        <Section title="Global Settings">
          <Item title="Name">
            <Field value={this.props.Organization.name} onChange={this.handleNameSave.bind(this)}/>
          </Item>
          <Item title="Timezone">
            <Select showSearch
                    filterOption={this.timezoneSearch.bind(this)}
                    value={this.props.Organization.tzName}
                    className="user-profile--organization-settings-timezones-select"
                    onChange={this.handleTimezoneChange.bind(this)}>
              {timezonesOptions}
            </Select>
          </Item>
        </Section>
        <Section title="Invite Users">
          <Item>
            <InviteUsersForm onSubmit={this.handleInviteSubmit.bind(this)}/>
          </Item>
        </Section>
        <Section title="Users">
          <Item>
            <OrganizationUsers/>
          </Item>
        </Section>
        <Section title="Branding">
          <Item>
            <OrganizationBranding/>
          </Item>
        </Section>
      </div>
    );
  }

}

export default OrganizationSettings;
