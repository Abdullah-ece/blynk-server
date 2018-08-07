import React from 'react';
import {Icon} from 'antd';
import PropTypes from 'prop-types';
import './styles.less';

class ConnectionLoading extends React.Component {

  static propTypes = {
    placeholder: PropTypes.string,
  };

  render() {
    return (
      <div className="connection-loading">
        <Icon type="loading"/>
        <div className="connection-loading--placeholder">
          {this.props.placeholder}
        </div>
      </div>
    );
  }

}

export default ConnectionLoading;
