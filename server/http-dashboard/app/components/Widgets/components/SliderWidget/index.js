import React from 'react';
import PropTypes from 'prop-types';
import Dotdotdot from 'react-dotdotdot';
import {WIDGETS_SLIDER_VALUE_POSITION} from 'services/Widgets';
import {Slider, Icon} from 'antd';
import Canvasjs from 'canvasjs';
import './styles.less';

import SliderWidgetSettings from './settings';

/*

why use widget data wrapper:
rerender only component SliderWidget
pass data to it based on

*/

class SliderWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,

    deviceId: PropTypes.any,

    style: PropTypes.object,

    onWidgetDelete: PropTypes.func,

    loading: PropTypes.oneOfType([
      PropTypes.bool,
      PropTypes.object,
    ]),

    value: PropTypes.string,

    parentElementProps: PropTypes.shape({
      id         : PropTypes.string,
      onMouseUp  : PropTypes.func,
      onTouchEnd : PropTypes.func,
      onMouseDown: PropTypes.func,
      style      : PropTypes.object,
    }),

    tools        : PropTypes.element,
    settingsModal: PropTypes.element,
    resizeHandler: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.element),
      PropTypes.element,
    ]),

  };

  constructor(props) {
    super(props);

  }

  formatValue(value) {
    if (!this.props.data.decimalFormat)
      return (value).toLocaleString();

    if (!isNaN(Number(value)) && Number(value) === 0)
      return 0;

    return Canvasjs.formatNumber(value, this.props.data.decimalFormat);
  }

  sliderWithControls(slider) {
    return (
      <div className="widgets--widget-slider-wrapper">
        <div className="widgets--widget-slider--control-left">
          <Icon type="minus"/>
        </div>
        <div className="widgets--widget-slider--control-slider">
          {slider}
        </div>
        <div className="widgets--widget-slider--control-right">
          <Icon type="plus"/>
        </div>
      </div>
    );
  }

  sliderWithoutControls(slider) {
    return (
      <div className="widgets--widget-slider-wrapper">
        <div className="widgets--widget-slider--control-slider widgets--widget-slider--control-slider-no-controls">
          {slider}
        </div>
      </div>
    );
  }

  sliderValueLeft(isNoData, sliderWrap, value, suffix) {

    const className = isNoData ? 'widgets--widget-slider-container--value--value--no-data': '';

    return (
      <div className="widgets--widget-slider-container">
        <div className="widgets--widget-slider-container--value widgets--widget-slider-container-value-left">
          <div className={`widgets--widget-slider-container--value--value ${className}`}>
            {value}
          </div>
          <div className="widgets--widget-slider-container--value--suffix">
            {suffix}
          </div>
        </div>
        <div className="widgets--widget-slider-container--slider">
          {sliderWrap}
        </div>
      </div>
    );
  }

  sliderValueRight(isNoData, sliderWrap, value, suffix) {

    const className = isNoData ? 'widgets--widget-slider-container--value--value--no-data': '';

    return (
      <div className="widgets--widget-slider-container">
        <div className="widgets--widget-slider-container--slider">
          { sliderWrap }
        </div>
        <div className="widgets--widget-slider-container--value widgets--widget-slider-container-value-right">
          <div className={`widgets--widget-slider-container--value--value ${className}`}>
            {value}
          </div>
          <div className="widgets--widget-slider-container--value--suffix">
            {suffix}
          </div>
        </div>
      </div>
    );
  }

  renderSliderByParams(params = {
    fineControlEnabled: false,
    valuePosition: WIDGETS_SLIDER_VALUE_POSITION.LEFT,
    value: null,
    suffix: null,
    step: 1,
    minValue: -1,
    maxValue: 1,
  }) {

    const isNoData = params.value === null || params.value === undefined;

    const value = isNoData ? '--' : params.value;
    const suffix = isNoData ? '' : params.suffix;

    const slider = (
      <Slider min={params.minValue} max={params.maxValue} step={params.step}/>
    );

    const position = params.valuePosition === WIDGETS_SLIDER_VALUE_POSITION.LEFT ? this.sliderValueLeft : this.sliderValueRight;

    const controls = params.fineControlEnabled ? this.sliderWithControls : this.sliderWithoutControls;

    return (
      <div className={`widgets--widget-slider`}>
        { position(isNoData, controls(slider), value, suffix) }
      </div>
    );
  }

  renderSlider() {

    const sliderValue = this.getValue();

    return this.renderSliderByParams({
      minValue: this.props.data.minValue,
      maxValue: this.props.data.maxValue,
      sendValuesOnRelease: this.props.data.sendValuesOnRelease,
      step: this.props.data.step,
      fineControlStep: this.props.data.fineControlStep,
      fineControlEnabled: this.props.data.fineControlEnabled,
      valuePosition: this.props.data.valuePosition,
      decimalFormat: this.props.data.decimalFormat,
      value: sliderValue,
      suffix: this.props.data.valueSuffix
    });
  }

  getValue() {
    return this.props.value;
  }

  render() {

    let style = {
      position:"relative",
      ...(this.props.parentElementProps && this.props.parentElementProps.style || {}),
      ...this.props.style,
    };

    return (
      <div {...this.props.parentElementProps} style={style} className={`widgets--widget widgets--widget-slider`}>
        <div className="widgets--widget-label" style={this.props.data.textColor === "DEFAULT"? {color:"#58595d"}: null} >
          <Dotdotdot  clamp={1}>{this.props.data.label || 'No Widget Name'}</Dotdotdot>
          {this.props.tools}
        </div>

        { /* widget content */ }

        { this.renderSlider() }

        { /* end widget content */ }

        {this.props.settingsModal}
        {this.props.resizeHandler}
      </div>
    );
  }

}

SliderWidget.Settings = SliderWidgetSettings;

export default SliderWidget;
