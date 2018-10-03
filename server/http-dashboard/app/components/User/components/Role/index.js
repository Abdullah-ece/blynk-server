import React from 'react';

import {Select} from 'antd';

import {Roles, SUPER_ADMIN_ROLE_ID} from 'services/Roles';

import './styles.less';

import {connect} from 'react-redux';

@connect((state) => ({
  roles: state.Organization.roles,
}))
export default class Role extends React.Component {

  static propTypes = {
    role: React.PropTypes.string,
    roles: React.PropTypes.any,
    onChange: React.PropTypes.func
  };

  constructor(props) {
    super(props);
  }

  getRolesList() {
    const options = [];
    this.props.roles.filter((role) => role && role.id && role.id !== SUPER_ADMIN_ROLE_ID).forEach((role) => {
      let key = `${role.id}`;
      options.push(<Select.Option key={key} value={key}>{role.name}</Select.Option>);
    });
    return options;
  }

  onChange(value) {
    if (this.props.onChange) this.props.onChange(value);
  }

  render() {

    const options = this.getRolesList();
    const role = this.props && this.props.role;

    return (
      (role === Roles.SUPER_ADMIN.value && <div>{Roles.SUPER_ADMIN.title}</div> ) || (
        <Select className="user--role-select"
                value={`${role}`}
                onChange={this.onChange.bind(this)} disabled={role === Roles.SUPER_ADMIN.value}>
        { options }
        </Select>)
    );
  }

}
