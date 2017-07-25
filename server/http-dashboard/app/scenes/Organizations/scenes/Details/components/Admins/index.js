import React            from 'react';
import {AdminTableList} from "scenes/Organizations/components/Manage/components/Admins/components";
import {
  List
}                       from "immutable";
import PropTypes        from 'prop-types';
import './styles.less';

class Admins extends React.Component {

  static propTypes = {
    users: PropTypes.instanceOf(List),

    onUsersDelete: PropTypes.func,

    loading: PropTypes.bool,
  };

  constructor(props) {
    super(props);

    this.handleAdminDelete = this.handleAdminDelete.bind(this);
  }

  handleAdminDelete(ids) {
    return this.props.onUsersDelete(ids.toJS());
  }

  render() {
    return (
      <div>
        <AdminTableList loading={this.props.loading} onAdminDelete={this.handleAdminDelete} data={this.props.users}/>
      </div>
    );
  }

}

export default Admins;
