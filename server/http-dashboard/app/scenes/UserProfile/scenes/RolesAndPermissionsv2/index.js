import React from 'react';
import { RolesAndPermissions } from "./components";
// import PropTypes from 'prop-types';
import { connect } from 'react-redux';

// import {bindActionCreators} from 'redux';

@connect((state) => ({
  roles: state.RolesAndPermissions
}))
class RolesAndPermissionsv2 extends React.Component {

  static propTypes = {};

  render() {
    console.log(this.props);
    return (
      <RolesAndPermissions roles={this.props.roles}/>
    );
  }

}

export default RolesAndPermissionsv2;
