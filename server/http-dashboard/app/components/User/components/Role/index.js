import React from 'react';

import {Select} from 'antd';

import {UsersAvailableRoles} from 'services/Roles';

import './styles.scss';

export default class Role extends React.Component {

  static propTypes = {
    role: React.PropTypes.string,
    onChange: React.PropTypes.func
  };

  getRolesList() {
    const options = [];
    UsersAvailableRoles.forEach((role) => {
      options.push(<Select.Option key={role.value} disabled={role.disabled}>{role.title}</Select.Option>);
    });
    return options;
  }

  onChange(value) {
    if (this.props.onChange) this.props.onChange(value);
  }

  render() {

    const options = this.getRolesList();

    return (
      <Select labelInValue className="user--role-select" value={{key: this.props.role}}
              onChange={this.onChange.bind(this)}>
        { options }
      </Select>
    );
  }

}
