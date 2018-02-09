import React, {Component} from 'react';
import PropTypes from 'prop-types';

import {Preview} from '../../components/index';

class PreviewScene extends Component {

  static propTypes = {
    data: PropTypes.object,
    deviceId: PropTypes.number,
  };

  render() {
    return(
      <Preview deviceId={this.props.deviceId}
               data={this.props.data}/>
    );
  }
}

export default PreviewScene;
