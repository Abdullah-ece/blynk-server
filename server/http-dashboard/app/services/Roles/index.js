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

const InviteAvailableRoles = [
  Roles.ADMIN,
  Roles.STAFF,
  Roles.USER
];

export {
  Roles,
  InviteAvailableRoles
};
