import React from 'react';
import { RolesAndPermissions } from "./components";
// import PropTypes from 'prop-types';
import { connect } from 'react-redux';

import { GetPermissions, UpdateRole } from 'data/RolesAndPermissions/actions';

import { bindActionCreators } from 'redux';

@connect((state) => ({
  roles: state.RolesAndPermissions.roles
}), (dispatch) => ({
  GetPermissions: bindActionCreators(GetPermissions, dispatch),
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
