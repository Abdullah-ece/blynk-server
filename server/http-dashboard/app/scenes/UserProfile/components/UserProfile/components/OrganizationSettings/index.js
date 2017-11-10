import React from 'react';

import {Item} from '../../../Section';
import {Select, Modal, message} from 'antd';
import Timezones from 'services/timeszones';
import Field from '../../../Field';

import './styles.less';

class OrganizationSettings extends React.Component {

  static propTypes = {
    Organization: React.PropTypes.object,
    onOrganizationUpdateName: React.PropTypes.func,
    onOrganizationSave: React.PropTypes.func,
    onOrganizationUpdateTimezone: React.PropTypes.func,
    Account: React.PropTypes.object
  };

  constructor(props) {
    super(props);

    this.handleNameSave = this.handleNameSave.bind(this);
    this.timezoneSearch = this.timezoneSearch.bind(this);
    this.handleTimezoneChange = this.handleTimezoneChange.bind(this);
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
    this.props.onOrganizationSave(Object.assign({}, this.props.Organization, {tzName: timezone})).then(() => {
      this.props.onOrganizationUpdateTimezone(timezone);
      hideUpdatingMessage();
    });
  }

  handleNameSave(name) {
    const hideUpdatingMessage = message.loading('Updating organization name..', 0);
    this.props.onOrganizationSave(Object.assign({}, this.props.Organization, {name: name})).then(() => {
      this.props.onOrganizationUpdateName(name);
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

        <Item title="Name">
          <Field value={this.props.Organization.name} onChange={this.handleNameSave}/>
        </Item>
        <Item title="Timezone">
          <Select showSearch
                  filterOption={this.timezoneSearch}
                  value={this.props.Organization.tzName}
                  className="user-profile--organization-settings-timezones-select"
                  onChange={this.handleTimezoneChange}>
            {timezonesOptions}
          </Select>
        </Item>


      </div>
    );
  }

}

export default OrganizationSettings;
