import _ from 'lodash';

export const TABS = {
  ACCOUNT_SETTINGS: {
    key:'accountSettings',
    value:'account settings',
  },
  ORGANIZATION_SETTINGS: {
    key:'organizationSettings',
    value:'organization settings',
  },
  USERS:{
    key:'users',
    value:'users',
  },
  BRANDING:{
    key:'branding',
    value:'branding',
  },
};

export const getTabValueByKey = (tabValue) => {
   const tabKey = _.findKey(TABS, {key:tabValue});

   return TABS[tabKey].value;
};
