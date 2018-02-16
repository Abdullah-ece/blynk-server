import React from 'react';
import PropTypes from 'prop-types';
import {Map} from 'immutable';
import {LinearWidget} from 'components/Widgets/components';
import {Icon} from 'antd';

import './styles.less';

class Preview extends React.Component {

  static propTypes = {
    data: PropTypes.object,

    deviceId: PropTypes.number,

    loading: PropTypes.oneOfType([
      PropTypes.bool,
      PropTypes.object,
    ]),

    history: PropTypes.instanceOf(Map),
  };

  renderLoading() {
    return (
      <div>
        <Icon type="loading" />
      </div>
    );
  }

  renderPreview() {

    return (
      <div className="line-chart-widget-preview">
        <LinearWidget   history={this.props.history}
                        data={this.props.data}
                        deviceId={this.props.deviceId}
                        loading={this.props.loading}/>
      </div>
    );
  }

  render() {

    if(this.props.loading)
      return this.renderLoading();

    return this.renderPreview();
  }

}

export default Preview;
