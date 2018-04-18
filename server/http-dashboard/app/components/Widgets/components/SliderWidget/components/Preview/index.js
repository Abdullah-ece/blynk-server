import React from 'react';
import PropTypes from 'prop-types';

import {LabelWidget} from 'components/Widgets/components';

import {
  Map,
} from 'immutable';

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

  renderPreview() {

    const labelStyles = {};

    if(this.props.data.backgroundColor)
      labelStyles.background = '#'+this.props.data.backgroundColor;

    if(this.props.data.textColor)
      labelStyles.color = '#'+this.props.data.textColor;

    return (
      <div className="label-widget-preview">
        <LabelWidget style={labelStyles}
                     deviceId={this.props.deviceId}
                     data={this.props.data} history={this.props.history} loading = {this.props.loading} />
      </div>
    );
  }

  render() {
    return this.renderPreview();
  }

}

export default Preview;
