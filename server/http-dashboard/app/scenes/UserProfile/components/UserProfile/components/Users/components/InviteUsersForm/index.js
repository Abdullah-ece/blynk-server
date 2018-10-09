import React from 'react';
import _ from 'lodash';
import {filterSuperAdmin, formatRolesToKeyValueList} from "services/Roles";

import InviteForm from './components';

import {connect} from 'react-redux';

@connect((state) => ({
  roles: state.Organization.roles,
}))
class InviteUsersForm extends React.Component {

  static propTypes = {
    roles: React.PropTypes.any,
    invalid: React.PropTypes.bool,
    pristine: React.PropTypes.bool,
    submitting: React.PropTypes.bool,
    handleSubmit: React.PropTypes.func,
    onSubmit: React.PropTypes.func,
    reset: React.PropTypes.func,
    error: React.PropTypes.string
  };

  constructor(props) {
    super(props);
  }

  render() {

    const initialValues = {
      role: `${_.first(filterSuperAdmin(this.props.roles)).id}`,
    };

    const rolesList = formatRolesToKeyValueList(
      filterSuperAdmin(this.props.roles)
    );

    return (
      <InviteForm onSubmit={this.props.onSubmit} initialValues={initialValues} rolesList={rolesList}/>
    );

  }

}

export default InviteUsersForm;
