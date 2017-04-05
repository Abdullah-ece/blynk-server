import React from 'react';

import {Title, Section, Item} from '../../index';
import {Select, Modal} from 'antd';

import TimeZones from './services/Timezones';

import Field from '../../components/Field';
import InviteUsersForm from './components/InviteUsersForm';
import OrganizationUsers from './components/OrganizationUsers';

import './styles.scss';

class OrganizationSettings extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      name: 'Blynk Inc.'
    };
  }

  showInviteSuccess() {
    Modal.success({
      title: 'Success',
      content: 'Invite has been sent to email!',
      okText: 'Ok'
    });
  }

  handleNameSave(name) {
    this.setState({name: name});
  }

  generateOptions() {
    const options = [];
    TimeZones.forEach((timezone, key) => {
      options.push(<Select.Option key={key}>{timezone.text}</Select.Option>);
    });
    return options;
  }

  render() {

    const timezonesOptions = this.generateOptions();

    return (
      <div className="user-profile">
        <Title text="Organization Settings"/>
        <Section title="Global Settings">
          <Item title="Name">
            <Field value={this.state.name} onChange={this.handleNameSave.bind(this)}/>
          </Item>
          <Item title="Timezone">
            <Select defaultValue="Select timezone" className="user-profile--organization-settings-timezones-select">
              {timezonesOptions}
            </Select>
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
      </div>
    );
  }

}

export default OrganizationSettings;
