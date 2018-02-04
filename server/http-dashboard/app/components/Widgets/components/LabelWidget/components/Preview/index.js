import React from 'react';
import PropTypes from 'prop-types';

import Dotdotdot from 'react-dotdotdot';

import {LabelWidget} from 'components/Widgets/components';

import {WIDGETS_LABEL_TEXT_ALIGNMENT} from 'services/Widgets';

import './styles.less';

class Preview extends React.Component {

  static propTypes = {

    data: PropTypes.object,

    deviceId: PropTypes.number,

    devicesLoading: PropTypes.bool,

    submitting: PropTypes.bool,

    handleSubmit: PropTypes.func,

    invalid: PropTypes.bool,
    pristine: PropTypes.bool
  };

  renderPreview() {

    const labelStyles = {};

    if(this.props.data.backgroundColor)
      labelStyles.background = '#'+this.props.data.backgroundColor;

    if(this.props.data.textColor)
      labelStyles.color = '#'+this.props.data.textColor;

    const getTextAlignStyle = (alignment) => {
      if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT)
        return 'left';

      if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.CENTER)
        return 'center';

      if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.RIGHT)
        return 'right';
    };

    labelStyles.textAlign = getTextAlignStyle(this.props.data.alignment);

    return (
      <div>

        <div className="widgets--widget label-widget-preview" style={labelStyles}>
          <div className="widgets--widget-label" style={{padding: 0}}>
            <Dotdotdot clamp={1}>{this.props.data.label || 'No Widget Name'}</Dotdotdot>
          </div>
          <LabelWidget fetchRealData={true}
                       deviceId={this.props.deviceId}
                       data={this.props.data}
                       isChartPreview={true}/>
        </div>

      </div>
    );
  }

  render() {
    return this.renderPreview();
  }

}

export default Preview;
