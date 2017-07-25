import React            from 'react';
import {
  AdminTableList,
  AdminInviteForm
}                       from "scenes/Organizations/components/Manage/components/Admins/components";
import {
  List
}                       from "immutable";
import PropTypes        from 'prop-types';
import './styles.less';

class Admins extends React.Component {

  static propTypes = {
    users: PropTypes.instanceOf(List),

    onUserAdd: PropTypes.func,
    onUsersDelete: PropTypes.func,
    onUserInviteSuccess: PropTypes.func,

    userInviteLoading: PropTypes.bool,
    userDeleteLoading: PropTypes.bool,
  };

  constructor(props) {
    super(props);

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleAdminDelete = this.handleAdminDelete.bind(this);
  }

  handleAdminDelete(ids) {
    return this.props.onUsersDelete(ids.toJS());
  }

  handleSubmit(values) {
    return this.props.onUserAdd(values);
  }

  render() {
    return (
      <div>
        <AdminInviteForm loading={this.props.userInviteLoading} onSubmit={this.handleSubmit}
                         onSubmitSuccess={this.props.onUserInviteSuccess}/>
        <AdminTableList loading={this.props.userDeleteLoading} onAdminDelete={this.handleAdminDelete}
                        data={this.props.users}/>
      </div>
    );
  }

}

export default Admins;
