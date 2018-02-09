import React, {Component} from 'react';
import PropTypes from 'prop-types';

import {SwitchWidget} from 'components/Widgets/components';

import './styles.less';

class Preview extends Component {

  static propTypes = {
    data: PropTypes.object,
    deviceId: PropTypes.number,
  };

  render() {

    return(
      <div className="switch-widget-preview">
        <SwitchWidget deviceId={this.props.deviceId}
                      data={this.props.data}/>
      </div>
    );
  }
}

export default Preview;
