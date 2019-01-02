import React from 'react';
import { RolesAndPermissions } from "./components";
// import PropTypes from 'prop-types';
import { connect } from 'react-redux';

import { GetPermissions, UpdateRole, GetRolesUsers } from 'data/RolesAndPermissions/actions';

import { bindActionCreators } from 'redux';

@connect((state) => ({
  roles: state.RolesAndPermissions.roles,
  usersPerRole: state.RolesAndPermissions.usersPerRole,
  permissions: state.RolesAndPermissions.currentRole.permissionGroup1,
}), (dispatch) => ({
  GetPermissions: bindActionCreators(GetPermissions, dispatch),
  GetRolesUsers: bindActionCreators(GetRolesUsers, dispatch),
  UpdateRole: bindActionCreators(UpdateRole, dispatch)
}))
class RolesAndPermissionsv2 extends React.Component {

  static propTypes = {};

  static defaultProps = {
    roles: []
  };

  render() {
    return (
      <RolesAndPermissions {...this.props}/>
    );
  }

}

export default RolesAndPermissionsv2;
