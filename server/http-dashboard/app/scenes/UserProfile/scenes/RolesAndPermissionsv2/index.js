import React from 'react';
import { RolesAndPermissions } from "./components";
// import PropTypes from 'prop-types';
import { connect } from 'react-redux';

import {GetPermissions} from 'data/RolesAndPermissions/actions';

import {bindActionCreators} from 'redux';

@connect((state) => ({
  roles: state.RolesAndPermissions.roles
}), (dispatch)=> ({
  GetPermissions: bindActionCreators(GetPermissions, dispatch)
}))
class RolesAndPermissionsv2 extends React.Component {

  static propTypes = {};

  static defaultProps = {
    roles: []
  }

  render() {
    console.log(this.props);
    return (
      <RolesAndPermissions {...this.props}/>
    );
  }

}

export default RolesAndPermissionsv2;
