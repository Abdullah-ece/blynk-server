import React          from 'react';
import {MainList, MainLayout}     from 'components';
import './styles.less';

import { VerifyPermission, PERMISSIONS_INDEX } from "services/Roles";
import { connect } from 'react-redux';
import PropTypes from "prop-types";

@connect((state) => ({
  permissions: state.RolesAndPermissions.currentRole.permissionGroup1,
}))
class Empty extends React.Component {
  static propTypes = {
    permissions: PropTypes.number,
  };

  render() {
    const canCreateOrgs = VerifyPermission(this.props.permissions, PERMISSIONS_INDEX.ORG_CREATE);

    return (
      <MainLayout.Content className="organizations-empty">
        <MainList>
          {canCreateOrgs ? <MainList.Empty title="Manage organizations easily"
                          description="Start with adding a new organization that will get access to the devices it owns."
                          link="/organizations/create"
                          btnText="Create New Organization"/> :
          <MainList.Empty title="Manage organizations easily"
                          description="Start with adding a new organization that will get access to the devices it owns."
                          />}
        </MainList>
      </MainLayout.Content>
    );
  }

}

export default Empty;
