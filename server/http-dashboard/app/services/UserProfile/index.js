import _ from 'lodash';

export const TABS = {
  ACCOUNT_SETTINGS: {
    key:'accountSettings',
    value:'Account Settings',
  },
  ORGANIZATION_SETTINGS: {
    key:'organizationSettings',
    value:'Organization Settings',
  },
  USERS:{
    key:'users',
    value:'Organization Users',
  },
  BRANDING:{
    key:'branding',
    value:'Organization Branding',
  },
};

export const getTabValueByKey = (tabValue) => {
   const tabKey = _.findKey(TABS, {key:tabValue});

   return TABS[tabKey].value;
};
