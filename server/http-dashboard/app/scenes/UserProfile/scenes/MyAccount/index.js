import React from 'react';

import Title from '../../components/Title';
import {Section, Item} from '../../components/Section';
import {Button, Modal} from 'antd';

import Field from '../../components/Field';

import './styles.scss';

class MyAccount extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      name: 'Albert Einstein',
      email: 'albert.einstein@gmail.com'
    };
  }

  handleNameSave(name) {
    this.setState({name: name});
  }

  showResetPasswordSuccessMessage() {
    Modal.success({
      title: 'Success',
      content: 'New password has been sent to your email!',
      okText: 'Ok'
    });
  }

  render() {
    return (
      <div className="user-profile">
        <Title text="My Account"/>
        <Section title="User Settings">
          <Item title="Name">
            <Field value={this.state.name} onChange={this.handleNameSave.bind(this)}/>
          </Item>
          <Item title="Email Address">
            {this.state.email}
          </Item>
        </Section>
        <Section title="Change Password">
          <Item>
            <Button type="primary" onClick={this.showResetPasswordSuccessMessage.bind(this)}>Send password reset
              email</Button>
          </Item>
        </Section>
      </div>
    );
  }

}

export default MyAccount;
