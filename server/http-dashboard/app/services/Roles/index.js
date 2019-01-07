export const SUPER_ADMIN_ROLE_ID = 0;
export const SUPER_ADMIN_ROLE_TITLE = 'Super Admin';

export const filterSuperAdmin = (roles = []) => {
  return roles.filter((role) => role && role.id && role.id !== SUPER_ADMIN_ROLE_ID);
};

export const formatRolesToKeyValueList = (roles) => {
  return roles.map((role) => {
    return {
      key: `${role.id}`,
      value: `${role.name}`
    };
  });
};

export const ORG_INVITE_ROLE_ID = 1;

const Roles = {
  'SUPER_ADMIN': {
    value: 'SUPER_ADMIN',
    title: 'Super Admin'
  },
  'ADMIN': {
    value: 'ADMIN',
    title: 'Admin'
  },
  'STAFF': {
    value: 'STAFF',
    title: 'Staff'
  },
  'USER': {
    value: 'USER',
    title: 'User'
  }
};

const AvailableRoles = {
  'ADMIN': {
    key: 'ADMIN',
    value: 'Admin'
  },
  'STAFF': {
    key: 'STAFF',
    value: 'Staff'
  },
  'USER': {
    key: 'USER',
    value: 'User'
  }
};

const InviteAvailableRoles = [
  AvailableRoles.ADMIN,
  AvailableRoles.STAFF,
  AvailableRoles.USER
];

const UsersAvailableRoles = [
  Roles.ADMIN,
  Roles.STAFF,
  Roles.USER
];

const MetadataRoles = [
  {
    key: Roles.ADMIN.value,
    value: Roles.ADMIN.title
  },
  {
    key: Roles.STAFF.value,
    value: Roles.STAFF.title
  },
  {
    key: Roles.USER.value,
    value: Roles.USER.title
  }
];

const MetadataRolesDefault = [];

export {
  MetadataRoles,
  MetadataRolesDefault,
  Roles,
  InviteAvailableRoles,
  UsersAvailableRoles
};

export const isUserAbleToEdit = (userRole, fieldRole) => {

  if (userRole === SUPER_ADMIN_ROLE_ID)
    return true;

  return (Array.isArray(fieldRole) ? fieldRole : [fieldRole]).indexOf(Number(userRole)) !== -1;
};

export const PERMISSIONS_INDEX = {
  ORG_SWITCH: 0,
  OWN_ORG_EDIT: 1,
  OTA_VIEW: 2,
  OTA_START: 3,
  OTA_STOP: 4,

  ORG_CREATE: 5,
  ORG_VIEW: 6,
  ORG_EDIT: 7,
  ORG_DELETE: 8,

  ORG_INVITE_USERS: 9,
  ORG_VIEW_USERS: 10,
  ORG_EDIT_USERS: 11,
  ORG_DELETE_USERS: 12,

  PRODUCT_CREATE: 13,
  PRODUCT_VIEW: 14,
  PRODUCT_EDIT: 15,
  PRODUCT_DELETE: 16,

  ROLE_CREATE: 17,
  ROLE_VIEW: 18,
  ROLE_EDIT: 19,
  ROLE_DELETE: 20,

  ORG_DEVICES_CREATE: 21,
  ORG_DEVICES_VIEW: 22,
  ORG_DEVICES_EDIT: 23,
  ORG_DEVICES_DELETE: 24,

  ORG_DEVICES_SHARE: 25,
  OWN_DEVICES_CREATE: 26,
  OWN_DEVICES_VIEW: 27,
  OWN_DEVICES_EDIT: 28,
  OWN_DEVICES_DELETE: 29,
  OWN_DEVICES_SHARE: 30,

  SET_AUTH_TOKEN: 31,
};

export const PERMISSIONS2_INDEX = {
  RULE_GROUP_VIEW: 0,
  RULE_GROUP_EDIT: 1,
};

export const PERMISSIONS_TABLE = [
  {
    dependentPermsToActivate: [6],
    dependentPermsToRemove: [],
    key: 'ORG_SWITCH',
    value: 'Switch to Sub-Organizations'
  },
  {
    dependentPermsToActivate: [6],
    dependentPermsToRemove: [],
    key: 'OWN_ORG_EDIT',
    value: 'Access Organization Settings'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'OTA_VIEW',
    value: 'Get Access'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'OTA_START',
    value: 'Initiate FOTA'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'OTA_STOP',
    value: 'Stop/Pause FOTA'
  },
  {
    dependentPermsToActivate: [6],
    dependentPermsToRemove: [],
    key: 'ORG_CREATE',
    value: 'Create'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [5, 7, 8, 0, 1],
    key: 'ORG_VIEW',
    value: 'View'
  },
  {
    dependentPermsToActivate: [6],
    dependentPermsToRemove: [],
    key: 'ORG_EDIT',
    value: 'Edit'
  },
  {
    dependentPermsToActivate: [6],
    dependentPermsToRemove: [],
    key: 'ORG_DELETE',
    value: 'Delete'
  },
  {
    dependentPermsToActivate: [10],
    dependentPermsToRemove: [],
    key: 'ORG_INVITE_USERS',
    value: 'Invite new users'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [9, 11, 12],
    key: 'ORG_VIEW_USERS',
    value: 'View'
  },
  {
    dependentPermsToActivate: [10],
    dependentPermsToRemove: [],
    key: 'ORG_EDIT_USERS',
    value: 'Edit'
  },
  {
    dependentPermsToActivate: [10],
    dependentPermsToRemove: [],
    key: 'ORG_DELETE_USERS',
    value: 'Delete'
  },
  {
    dependentPermsToActivate: [14],
    dependentPermsToRemove: [],
    key: 'PRODUCT_CREATE',
    value: 'Add new'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [13, 15, 16],
    key: 'PRODUCT_VIEW',
    value: 'View'
  },
  {
    dependentPermsToActivate: [14],
    dependentPermsToRemove: [],
    key: 'PRODUCT_EDIT',
    value: 'Edit'
  },
  {
    dependentPermsToActivate: [14],
    dependentPermsToRemove: [],
    key: 'PRODUCT_DELETE',
    value: 'Delete'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'ROLE_CREATE',
    value: 'Create new roles'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'ROLE_VIEW',
    value: 'View roles and permissions'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'ROLE_EDIT',
    value: 'Edit roles'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'ROLE_DELETE',
    value: 'Delete roles'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'ORG_DEVICES_CREATE',
    value: 'Add new devices'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [23, 24, 31],
    key: 'ORG_DEVICES_VIEW',
    value: 'View'
  },
  {
    dependentPermsToActivate: [22],
    dependentPermsToRemove: [31],
    key: 'ORG_DEVICES_EDIT',
    value: 'Edit'
  },
  {
    dependentPermsToActivate: [22],
    dependentPermsToRemove: [],
    key: 'ORG_DEVICES_DELETE',
    value: 'Delete'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'ORG_DEVICES_SHARE',
    value: 'Share access'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'OWN_DEVICES_CREATE',
    value: 'Add new devices'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [28, 29, 31],
    key: 'OWN_DEVICES_VIEW',
    value: 'View'
  },
  {
    dependentPermsToActivate: [27],
    dependentPermsToRemove: [31],
    key: 'OWN_DEVICES_EDIT',
    value: 'Edit'
  },
  {
    dependentPermsToActivate: [27],
    dependentPermsToRemove: [],
    key: 'OWN_DEVICES_DELETE',
    value: 'Delete'
  },
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [],
    key: 'OWN_DEVICES_SHARE',
    value: 'Share access'
  },
  {
    dependentPermsToActivate: [27, 28],
    dependentPermsToRemove: [],
    key: 'SET_AUTH_TOKEN',
    value: 'Enable Auth Token Edit'
  },
];

export const PERMISSIONS2_TABLE = [
  {
    dependentPermsToActivate: [],
    dependentPermsToRemove: [1],
    key: 'RULE_GROUP_VIEW',
    value: 'View Rule Group'
  },
  {
    dependentPermsToActivate: [0],
    dependentPermsToRemove: [],
    key: 'RULE_GROUP_EDIT',
    value: 'Edit Rule Group'
  },
];

export const VerifyPermission = (userPermissions, permissionToCheck) => {
  const newPermissions = Math.pow(2, permissionToCheck);
  let result = (userPermissions & newPermissions);
  result = result < 0 ? result * -1 : result;
  return result == newPermissions;
};
