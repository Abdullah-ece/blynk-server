import React            from 'react';
import {
  AdminTableList,
  AdminInviteForm
}                       from "scenes/Organizations/components/Manage/components/Admins/components";
import PropTypes        from 'prop-types';
import './styles.less';

class Admins extends React.Component {

  static propTypes = {
    inviteForm: PropTypes.object,
    tableList: PropTypes.object
  };

  constructor(props) {
    super(props);
  }

  render() {

    return (
      <div>
        <AdminInviteForm {...this.props.inviteForm}/>
        <AdminTableList {...this.props.tableList}/>
      </div>
    );
  }

}

export default Admins;
