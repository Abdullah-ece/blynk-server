import React from 'react';

import {Select} from 'antd';

import './styles.scss';

export default class Role extends React.Component {

  static propTypes = {
    role: React.PropTypes.string
  };

  render() {
    return (
      <Select labelInValue className="user--role-select" defaultValue={{key: this.props.role}}>
        <Select.Option value="admin">Admin</Select.Option>
        <Select.Option value="staff">Staff</Select.Option>
        <Select.Option value="readonly">Read Only</Select.Option>
      </Select>
    );
  }

}
