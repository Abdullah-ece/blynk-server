import React            from 'react';
import Admins           from "scenes/Organizations/components/Admins";
import {
  Map
}                       from "immutable";
import PropTypes        from 'prop-types';
import {connect}  from 'react-redux';
import {
  Manage
} from 'services/Organizations';
import {
  bindActionCreators
} from 'redux';
import {
  OrganizationSendInvite,
  OrganizationUsersDelete
} from 'data/Organization/actions';
import {
  OrganizationsUsersFetch,
  OrganizationsAdminsInviteLoadingToggle,
  OrganizationsAdminsDeleteLoadingToggle,
} from 'data/Organizations/actions';
import {ORG_INVITE_ROLE_ID} from 'services/Roles';
import {
  reset,
  SubmissionError,
} from 'redux-form';
import {
  message
} from 'antd';

@connect((state) => ({
  admins: state.Organizations.get('adminsEdit')
}), (dispatch) => ({
  resetForm: bindActionCreators(reset, dispatch),
  OrganizationSendInvite: bindActionCreators(OrganizationSendInvite, dispatch),
  OrganizationsUsersFetch: bindActionCreators(OrganizationsUsersFetch, dispatch),
  OrganizationUsersDelete: bindActionCreators(OrganizationUsersDelete, dispatch),
  OrganizationsAdminsInviteLoadingToggle: bindActionCreators(OrganizationsAdminsInviteLoadingToggle, dispatch),
  OrganizationsAdminsDeleteLoadingToggle: bindActionCreators(OrganizationsAdminsDeleteLoadingToggle, dispatch),
}))
class AdminsEditScene extends React.Component {

  static propTypes = {
    admins: PropTypes.instanceOf(Map),

    params: PropTypes.object,

    allowResendInvite: PropTypes.bool,

    orgId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

    resetForm: PropTypes.func,
    OrganizationSendInvite: PropTypes.func,
    OrganizationsUsersFetch: PropTypes.func,
    OrganizationUsersDelete: PropTypes.func,
    OrganizationsAdminsInviteLoadingToggle: PropTypes.func,
    OrganizationsAdminsDeleteLoadingToggle: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.handleAdminAdd = this.handleAdminAdd.bind(this);
    this.handleAdminAddSuccess = this.handleAdminAddSuccess.bind(this);
    this.handleAdminDelete = this.handleAdminDelete.bind(this);
  }

  componentWillMount() {
    if (!this.props.admins.get('users'))
      this.props.OrganizationsUsersFetch();
  }

  handleAdminDelete(ids) {
    this.props.OrganizationsAdminsDeleteLoadingToggle(true);
    return new Promise((resolve) => {
      this.props.OrganizationUsersDelete(this.props.params.id, ids).then(() => {
        this.props.OrganizationsUsersFetch({
          id: this.props.params.id
        }).then(() => {
          this.props.OrganizationsAdminsDeleteLoadingToggle(false);
          resolve(true);
        });
      });
    });
  }

  handleAdminAdd(user) {
    this.props.OrganizationsAdminsInviteLoadingToggle(true);

    return (new Promise((resolve, reject) => {
      this.props.OrganizationSendInvite({
        id: this.props.params.id,
        email: user.email,
        name: user.name,
        roleId: ORG_INVITE_ROLE_ID,
      }).then(() => {
        this.props.OrganizationsUsersFetch({
          id: this.props.params.id
        }).then(() => {
          this.props.OrganizationsAdminsInviteLoadingToggle(false);

          resolve();
        });
      }).catch((response) => {
        const data = response.error.response.data;

        this.props.OrganizationsAdminsInviteLoadingToggle(false);

        reject(data);
      });
    })).catch((data) => {

      if (data && data.error && data.error.message) {
        throw new SubmissionError({'email': data.error.message});
      } else {
        message.error(data && data.error && data.error.message || 'Cannot invite user');
        throw new SubmissionError();
      }

    });
  }

  handleAdminAddSuccess() {
    message.success('Invite has been sent');
    this.props.resetForm(Manage.ADMIN_INVITE_FORM_NAME);
  }

  render() {

    if (!this.props.admins.get('users'))
      return null;

    const inviteFormProps = {
      addText: 'Send',
      loading: this.props.admins.get('userInviteLoading'),
      onSubmit: this.handleAdminAdd,
      onSubmitSuccess: this.handleAdminAddSuccess,
    };

    const tableListProps = {
      loading: this.props.admins.get('userDeleteLoading'),
      onAdminDelete: this.handleAdminDelete,
      data: this.props.admins.get('users').filter(user => user.get('roleId') === ORG_INVITE_ROLE_ID),
      allowResendInvite: this.props.allowResendInvite,
      orgId: this.props.orgId,
    };

    return (
      <Admins inviteForm={inviteFormProps} tableList={tableListProps}/>
    );
  }

}

export default AdminsEditScene;
