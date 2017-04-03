import React from 'react';

import Title from '../../components/Title';
import {Section, Item} from '../../components/Section';
import {Select} from 'antd';

import TimeZones from './services/Timezones';

import Field from '../../components/Field';

import './styles.scss';

class OrganizationSettings extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      name: 'Blynk Inc.'
    };
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
      </div>
    );
  }

}

export default OrganizationSettings;
