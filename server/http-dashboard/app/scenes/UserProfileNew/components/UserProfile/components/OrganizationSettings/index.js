import React from 'react';

import {Section, Item} from '../../../Section';
import {Select, Modal, message} from 'antd';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import Timezones from 'services/timeszones';
import Field from '../../../Field';

import {reset} from 'redux-form';
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

  showInviteError(message) {
    Modal.error({
      title: 'Ooops!',
      content: String(message)
    });
  }

  showInviteSuccess() {
    message.success('Invite has been sent to email!');
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

      </div>
    );
  }

}

export default OrganizationSettings;
