import React from 'react';

import './styles.scss';

export default class Status extends React.Component {

  static propTypes = {
    status: React.PropTypes.number
  };

  render() {
    return (
      <div className="user--status">
        { this.props.status === 0 && <div className="user--status-pending">Pending</div> }
        { this.props.status === 1 && <div className="user--status-active">Active</div> }
      </div>
    );
  }

}
