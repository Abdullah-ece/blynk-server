import React from 'react';

import {Title, Section, Item} from '../../index';
import {Select, Modal, message} from 'antd';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import Timezones from 'services/timeszones';
import Field from '../../components/Field';
import InviteUsersForm from './components/InviteUsersForm';
import OrganizationUsers from './components/OrganizationUsers';
import OrganizationBranding from './components/OrganizationBranding';

import {
  OrganizationFetch,
  OrganizationUpdateName,
  OrganizationSave,
  OrganizationUpdateTimezone
} from 'data/Organization/actions';

import './styles.scss';

@connect((state) => ({
  Organization: state.Organization
}), (dispatch) => ({
  OrganizationFetch: bindActionCreators(OrganizationFetch, dispatch),
  OrganizationUpdateName: bindActionCreators(OrganizationUpdateName, dispatch),
  OrganizationSave: bindActionCreators(OrganizationSave, dispatch),
  OrganizationUpdateTimezone: bindActionCreators(OrganizationUpdateTimezone, dispatch),
}))
class OrganizationSettings extends React.Component {

  static propTypes = {
    Organization: React.PropTypes.object,
    OrganizationFetch: React.PropTypes.func,
    OrganizationUpdateName: React.PropTypes.func,
    OrganizationSave: React.PropTypes.func,
    OrganizationUpdateTimezone: React.PropTypes.func,
  };

  constructor(props) {
    super(props);

    props.OrganizationFetch();
  }

  showInviteSuccess() {
    Modal.success({
      title: 'Success',
      content: 'Invite has been sent to email!',
      okText: 'Ok'
    });
  }

  handleTimezoneChange(timezone) {
    const hideUpdatingMessage = message.loading('Updating organization timezone...', 0);
    this.props.OrganizationUpdateTimezone(timezone);
    /** @todo track error */
    this.props.OrganizationSave(Object.assign({}, this.props.Organization, {tzName: timezone})).then(() => {
      hideUpdatingMessage();
    });
  }

  handleNameSave(name) {
    const hideUpdatingMessage = message.loading('Updating organization name..', 0);
    this.props.OrganizationUpdateName(name);
    /** @todo track error */
    this.props.OrganizationSave(Object.assign({}, this.props.Organization, {name: name})).then(() => {
      hideUpdatingMessage();
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
    return option.props.children.indexOf(input) >= 0;
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
            { this.props.Organization.tzName &&
            <Select showSearch
                    filterOption={this.timezoneSearch.bind(this)}
                    defaultValue={this.props.Organization.tzName}
                    className="user-profile--organization-settings-timezones-select"
                    onChange={this.handleTimezoneChange.bind(this)}>
              {timezonesOptions}
            </Select>
            }
          </Item>
        </Section>
        <Section title="Invite Users">
          <Item>
            <InviteUsersForm onSubmit={this.showInviteSuccess.bind(this)}/>
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
