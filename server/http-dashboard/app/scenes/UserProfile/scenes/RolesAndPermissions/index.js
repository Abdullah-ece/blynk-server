import React from 'react';
import {RolesAndPermissions} from "./components";
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
// import {bindActionCreators} from 'redux';
import {getFormValues} from 'redux-form';

@connect((state) => ({
  formValues: getFormValues('ROLES_AND_PERMISSIONS')(state)
}))
class RolesAndPermissionsScene extends React.Component {

  static propTypes = {
    formValues: PropTypes.shape({
      roles: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.number,
        name: PropTypes.string,
        devices: PropTypes.any,
        products: PropTypes.any,
        suborganizations: PropTypes.any,
        organization: PropTypes.any,
      }))
    })
  };

  render() {

    const initialValues = {
      roles: [
        {
          id              : 1,
          name            : 'Super Admin',
          devices         : 111,
          products        : 111,
          suborganizations: 111,
          organization    : 111,
          isDefault        : true,
        },
        {
          id              : 2,
          name            : 'Admin',
          devices         : 111,
          products        : 111,
          suborganizations: 100,
          organization    : 111,
          isDefault        : true,
        },
        {
          id              : 3,
          name            : 'Staff',
          devices         : 111,
          products        : 0,
          suborganizations: 0,
          organization    : 0,
          isDefault        : true,
        },
        {
          id              : 4,
          name            : 'User',
          devices         : 100,
          products        : 0,
          suborganizations: 0,
          organization    : 0,
          isDefault        : true,
        },
        {
          id              : 5,
          name            : 'Role 1',
          devices         : 100,
          products        : 0,
          suborganizations: 0,
          organization    : 0
        },
        {
          id              : 6,
          name            : 'Role 2',
          devices         : 100,
          products        : 0,
          suborganizations: 0,
          organization    : 0
        }
      ]
    };

    return (
      <RolesAndPermissions form="ROLES_AND_PERMISSIONS" formValues={this.props.formValues} initialValues={initialValues}/>
    );
  }

}

export default RolesAndPermissionsScene;
