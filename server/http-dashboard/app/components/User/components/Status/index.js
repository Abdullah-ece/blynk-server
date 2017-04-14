import React from 'react';

import './styles.less';

export default class Status extends React.Component {

  static propTypes = {
    status: React.PropTypes.string
  };

  render() {
    return (
      <div className="user--status">
        { this.props.status === "Pending" && <div className="user--status-pending">Pending</div> }
        { this.props.status === "Active" && <div className="user--status-active">Active</div> }
      </div>
    );
  }

}
