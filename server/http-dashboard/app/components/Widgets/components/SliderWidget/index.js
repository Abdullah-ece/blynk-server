import React from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import Dotdotdot from 'react-dotdotdot';
import {WIDGETS_SLIDER_VALUE_POSITION} from 'services/Widgets';
import {Slider, Icon} from 'antd';
import Canvasjs from 'canvasjs';
import './styles.less';
import _ from 'lodash';

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
    onWriteToVirtualPin: PropTypes.func,

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

    this.state = {
      value: null,
      isDragging: false
    };

    this.handleChange = this.handleChange.bind(this);
    this.handleAfterChange = this.handleAfterChange.bind(this);
    this.writeToVirtualPin = _.throttle(this.writeToVirtualPin, 100);

    this.sliderRef = null;

    this.sliderValueRight = this.sliderValueRight.bind(this);
    this.sliderValueLeft = this.sliderValueLeft.bind(this);
    this.toggleSliderColor = this.toggleSliderColor.bind(this);
    this.sliderWithControls = this.sliderWithControls.bind(this);
    this.handleFineControlIncrease = this.handleFineControlIncrease.bind(this);
    this.handleFineControlDecrease = this.handleFineControlDecrease.bind(this);
    this.handleFineControlIncreaseMouseUp = this.handleFineControlIncreaseMouseUp.bind(this);
    this.handleFineControlIncreaseMouseDown = this.handleFineControlIncreaseMouseDown.bind(this);

    this.handleFineControlDecreaseMouseUp = this.handleFineControlDecreaseMouseUp.bind(this);
    this.handleFineControlDecreaseMouseDown = this.handleFineControlDecreaseMouseDown.bind(this);

  }

  componentWillMount() {
    this.setState({
      value: this.props.value
    });
  }

  componentDidMount() {
    this.toggleSliderColor();
  }

  componentWillReceiveProps(nextProps) {
    if(nextProps.value && !this.state.isDragging) {
      this.setState({
        value: nextProps.value
      });
    }

  }

  componentDidUpdate() {
    this.toggleSliderColor();
  }

  handleFineControlDecreaseMouseDown(event) {
    event.stopPropagation();

    this.fineControlDecreaseTimeout = setTimeout(() => {
      this.fineControlDecreaseInterval = setInterval(() => {
        this.handleFineControlDecrease();
      }, 100);
    }, 60);
  }

  handleFineControlDecreaseMouseUp(event) {
    event.stopPropagation();

    clearTimeout(this.fineControlDecreaseTimeout);
    clearInterval(this.fineControlDecreaseInterval);

    this.handleFineControlDecrease();
  }

  handleFineControlIncreaseMouseDown(event) {
    event.stopPropagation();

    this.fineControlIncreaseTimeout = setTimeout(() => {
      this.fineControlIncreaseInterval = setInterval(() => {
        this.handleFineControlIncrease();
      }, 100);
    }, 60);
  }

  handleFineControlIncreaseMouseUp(event) {
    event.stopPropagation();

    clearTimeout(this.fineControlIncreaseTimeout);
    clearInterval(this.fineControlIncreaseInterval);

    this.handleFineControlIncrease();
  }

  toggleSliderColor() {
    if(this.sliderRef) {
      let slider = ReactDOM.findDOMNode(this.sliderRef);
      if (slider) {
        slider.querySelector('.ant-slider-track').style.setProperty('background-color', '#' + this.props.data.color);
        slider.querySelector('.ant-slider-handle').pseudoStyle('before', 'background', '#' + this.props.data.color + ' !important');
      }
    }
  }

  formatValue(value) {
    if (!this.props.data.decimalFormat)
      return (value).toLocaleString();

    if (!isNaN(Number(value)) && Number(value) === 0)
      return 0;

    let format = Canvasjs.formatNumber(value, this.props.data.decimalFormat);

    if(!isNaN(Number(format))) {
      format = Number(format).toLocaleString();
    }

    return format;
  }

  sliderWithControls(slider) {
    return (
      <div className="widgets--widget-slider-wrapper">
        <div className="widgets--widget-slider--control-left" onMouseDown={this.handleFineControlDecreaseMouseDown} onMouseUp={this.handleFineControlDecreaseMouseUp}>
          <Icon type="minus"/>
        </div>
        <div className="widgets--widget-slider--control-slider">
          {slider}
        </div>
        <div className="widgets--widget-slider--control-right" onMouseDown={this.handleFineControlIncreaseMouseDown} onMouseUp={this.handleFineControlIncreaseMouseUp}>
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

  sliderValueLeft(isNoData, width, sliderWrap, value, suffix) {

    const className = isNoData ? 'widgets--widget-slider-container--value--value--no-data': '';

    return (
      <div className="widgets--widget-slider-container">
        <div className="widgets--widget-slider-container--value widgets--widget-slider-container-value-left" style={{minWidth: width, maxWidth: width}}>
          <div className={`widgets--widget-slider-container--value--value ${className}`}>
            {this.formatValue(value)}
          </div>
          <div className="widgets--widget-slider-container--value--suffix">
            {suffix}
          </div>
        </div>
        <div className="widgets--widget-slider-container--slider" onMouseMove={this.toggleSliderColor} onMouseLeave={this.toggleSliderColor}>
          {sliderWrap}
        </div>
      </div>
    );
  }

  handleFineControlIncrease() {
    let value = Number(this.getValue());
    let step = Number(this.props.data.fineControlStep);

    this.handleAfterChange(value + step);
  }

  handleFineControlDecrease() {
    let value = Number(this.getValue());
    let step = Number(this.props.data.fineControlStep);

    this.handleAfterChange(value - step);
  }

  sliderValueRight(isNoData, width, sliderWrap, value, suffix) {

    const className = isNoData ? 'widgets--widget-slider-container--value--value--no-data': '';

    return (
      <div className="widgets--widget-slider-container">
        <div className="widgets--widget-slider-container--slider" onMouseLeave={this.toggleSliderColor} onMouseMove={this.toggleSliderColor}>
          { sliderWrap }
        </div>
        <div className="widgets--widget-slider-container--value widgets--widget-slider-container-value-right" style={{minWidth: width, maxWidth: width}}>
          <div className={`widgets--widget-slider-container--value--value ${className}`}>
            {this.formatValue(value)}
          </div>
          <div className="widgets--widget-slider-container--value--suffix">
            {suffix}
          </div>
        </div>
      </div>
    );
  }

  writeToVirtualPin(value) {
    if(!this.props.onWriteToVirtualPin)
      return false;

    if (this.props.data.sources && this.props.data.sources.length && this.props.data.sources[0].dataStream) {

      const pin = this.props.data.sources[0].dataStream.pin;

      this.props.onWriteToVirtualPin({
        pin  : pin,
        value: value
      });

    }
  }

  getValueWidth(minValue, maxValue, suffix, step, fineStep) {
    let suffixWidthOfSymbol = 11;
    let marginWidth = 4;
    let valueWidthOfSymbol = 12;

    let stepControlSymbols = String(step).split('.').length > 1 ? String(step).split('.')[1].length : 0;
    let fineControlSymbols = String(fineStep).split('.').length > 1 ? String(fineStep).split('.')[1].length : 0;

    let minValueSymbols = String(this.formatValue(Number(minValue)) || '').length;
    let maxValueSymbols = String(this.formatValue(Number(maxValue)) || '').length;
    let suffixSymbols = String(suffix || '').length;

    let valueSymbols = Math.max(minValueSymbols,maxValueSymbols) + Math.max(stepControlSymbols, fineControlSymbols);

    return valueSymbols * valueWidthOfSymbol + suffixWidthOfSymbol * suffixSymbols + marginWidth;
  }

  handleChange(value, onRelease = false) {

    // disable value increase/decrease if value is out of scope
    if(value > this.props.data.maxValue || value < this.props.data.minValue) {
      return false;
    }

    if(!this.state.isDragging) {
      this.setState({
        isDragging: true
      });
    }

    this.setState({
      value: value
    });

    if(!this.props.data.sendOnReleaseOn || onRelease === true) {
      this.writeToVirtualPin(value);
    }

  }

  handleAfterChange(value) {
    this.handleChange(value, true);

    this.setState({
      isDragging: false
    });
  }

  renderSliderByParams(params = {
    fineControlEnabled: false,
    valuePosition: WIDGETS_SLIDER_VALUE_POSITION.LEFT,
    value: null,
    suffix: null,
    step: 1,
    fineControlStep: 1,
    minValue: -1,
    maxValue: 1,
  }) {

    const numberCheck = (value, defaultValue) => !isNaN(Number(value)) ? Number(value) : Number(defaultValue);

    const isNoData = params.value === null || params.value === undefined;

    const value = isNoData ? '--' : params.value;
    const suffix = isNoData ? '' : params.suffix;

    let sliderValue = numberCheck(value, 0);
    let minValue = numberCheck(params.minValue, 0);
    let maxValue = numberCheck(params.maxValue, 100);
    let step = numberCheck(params.step, 1);

    if(Number(sliderValue) < Number(params.minValue)) {
      sliderValue = Number(params.minValue);
    }

    if(Number(sliderValue) > Number(params.maxValue)) {
      sliderValue = Number(params.maxValue);
    }

    const slider = (
      <Slider included={true}  tipFormatter={null} ref={(ref) => this.sliderRef = ref} min={Number(minValue)} max={Number(maxValue)} step={step} value={sliderValue} onChange={this.handleChange} onAfterChange={this.handleAfterChange}/>
    );

    const position = params.valuePosition === WIDGETS_SLIDER_VALUE_POSITION.LEFT ? this.sliderValueLeft : this.sliderValueRight;

    const controls = params.fineControlEnabled ? this.sliderWithControls : this.sliderWithoutControls;

    let width = this.getValueWidth(minValue, maxValue, suffix, step, params.fineControlStep);

    return (
      <div className={`widgets--widget-slider`}>
        { position(isNoData, width, controls(slider), value, suffix) }
      </div>
    );
  }

  renderSlider() {

    const sliderValue = this.getValue();

    return this.renderSliderByParams({
      minValue: this.props.data.minValue,
      maxValue: this.props.data.maxValue,
      sendOnReleaseOn: this.props.data.sendOnReleaseOn,
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
    return this.state.value;
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
