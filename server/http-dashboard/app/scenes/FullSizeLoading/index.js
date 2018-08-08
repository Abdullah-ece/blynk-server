import React from 'react';
import {Icon} from 'antd';
import PropTypes from 'prop-types';
import './styles.less';

class FullSizeLoading extends React.Component {

  static propTypes = {
    placeholder: PropTypes.string,
  };

  render() {
    return (
      <div className="full-size-loading">
        <Icon type="loading"/>
        <div className="full-size-loading--placeholder">
          {this.props.placeholder}
        </div>
      </div>
    );
  }

}

export default FullSizeLoading;
