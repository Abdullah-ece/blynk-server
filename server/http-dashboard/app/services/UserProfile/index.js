import _ from 'lodash';

export const TABS = {
  ACCOUNT_SETTINGS: {
    key:'account-settings',
    value:'My Profile',
  },
  ORGANIZATION_SETTINGS: {
    key:'organization-settings',
    value:'Organization Settings',
  },
  USERS:{
    key:'users',
    value:'Users',
  },
  BRANDING:{
    key:'branding',
    value:'Branding',
  },
  ROLES_AND_PERMISSIONS: {
    key:'roles-and-permissions',
    value:'Roles & Permissions',
  }
};

export const getTabValueByKey = (tabValue) => {
   const tabKey = _.findKey(TABS, {key:tabValue});

   return TABS[tabKey].value;
};
