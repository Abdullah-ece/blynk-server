import React from 'react';
import PropTypes from 'prop-types';
import {Map, fromJS} from 'immutable';
import {Icon} from 'antd';
import Dotdotdot from 'react-dotdotdot';
import {WIDGETS_LABEL_TEXT_ALIGNMENT} from 'services/Widgets';
import Canvasjs from 'canvasjs';
import './styles.less';

import LabelWidgetSettings from './settings';

class LabelWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,

    deviceId: PropTypes.any,

    style: PropTypes.object,

    onWidgetDelete: PropTypes.func,

    loading: PropTypes.oneOfType([
      PropTypes.bool,
      PropTypes.object,
    ]),

    history: PropTypes.instanceOf(Map),

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

    this.generateData = this.generateData.bind(this);
  }

  getTextAlignmentClassNameByAlignment(alignment) {
    if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT)
      return 'widgets--widget-web-label--alignment-left';

    if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.CENTER)
      return 'widgets--widget-web-label--alignment-center';

    if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.RIGHT)
      return 'widgets--widget-web-label--alignment-right';
  }

  getValueClassName(isStringValue) {
    if (isStringValue)
      return 'widgets--widget-web-label--string-value';

    return 'widgets--widget-web-label--number-value';
  }

  getValueSizeClassName(cellSize) {
    if (Number(cellSize) === 1)
      return 'widgets--widget-web-label--value-size-1';

    if (Number(cellSize) === 2)
      return 'widgets--widget-web-label--value-size-2';

    if (Number(cellSize) >= 3)
      return 'widgets--widget-web-label--value-size-3';
  }

  formatLabelValue(value) {
    if (!this.props.data.decimalFormat)
      return (value).toLocaleString();

    return Canvasjs.formatNumber(value, this.props.data.decimalFormat);
  }

  renderLabelByParams(params = {alignment: WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT, value: null, suffix: null}) {

    const alignmentClassName = this.getTextAlignmentClassNameByAlignment(params.alignment);

    const isNoData = params.value === null || params.value === undefined;

    const isStringValue = isNaN(Number(params.value));

    const valueClassName = this.getValueClassName(isStringValue);

    const valueSizeClassName = this.getValueSizeClassName(this.props.data.height);

    return (
      <div className={`widgets--widget-web-label ${alignmentClassName}`}>
        {!isNoData && (
          <div className={`widgets--widget-web-label--container ${valueSizeClassName}`}>
            <Dotdotdot clamp={1}>
              <span
                className={`${valueClassName}`}>{isStringValue ? params.value : this.formatLabelValue(params.value)}</span>
              {params.suffix && (
                <span className="widgets--widget-web-label--suffix">{params.suffix || null}</span>
              )}
            </Dotdotdot>
          </div>
        ) || (
          <div className={`widgets--widget-web-label--container ${valueSizeClassName}`}>
            <span className={`widgets--widget-web-label--number-value`}>--</span>
          </div>
        )}
      </div>
    );
  }

  generateData(source, sourceIndex) {
    if (!source.has('dataStream') || !source.hasIn(['dataStream', 'pin']))
      return null;

    const pin = this.props.history.get(String(sourceIndex));

    if (!pin)
      return null;

    const lastPoint = pin.get('data').last();

    return lastPoint && lastPoint.get('y') || null;
  }

  renderRealDataLabel() {

    const labelValue = this.getLabelValue();

    if (labelValue === null)
      return (<div className="bar-chart-widget-no-data">No Data</div>);

    if (this.props.loading)
      return (<Icon type="loading"/>);

    return this.renderLabelByParams({
      value: labelValue,
      suffix: this.props.data.valueSuffix,
      alignment: this.props.data.alignment
    });
  }

  getLabelValue() {

    if (!this.props.data.sources || !this.props.data.sources.length || !this.props.history || this.props.loading === undefined)
      return null;

    const sources = fromJS(this.props.data.sources);

    const dataSources = sources.map(this.generateData).filter((source) => source !== null);

    return dataSources.get(0) || null;
  }

  getLabelStyles() {
    const labelValue = this.getLabelValue();
    const currentColorSet = (this.props.data.colorsSet.filter(( obj )=>(obj.min <= labelValue && obj.max >= labelValue)))[0] || {backgroundColor:"ffffff",textColor:"black"};

    return !this.props.data.isColorSetEnabled ? {
      backgroundColor: "#" + this.props.data.backgroundColor,
      color: "#" + this.props.data.textColor
    } : (({ backgroundColor,textColor }) => {
      return {
        backgroundColor: "#" + backgroundColor,
        color: "#" + textColor
      };
    })(currentColorSet);
  }

  renderLabelLevel() {

    if(this.props.data.level.min >= this.props.data.level.max){

      return null;
    }

    const percentFilled =  Math.round((this.getLabelValue() - this.props.data.level.min) / ((this.props.data.level.max - this.props.data.level.min) / 100));
    let style = {
      position: "absolute",
      bottom: 0,
        left: 0,
      backgroundColor: "#" + this.props.data.level.color,
      height: "100%",
      width: "100%",
    };

    if(this.props.data.level.position === "VERTICAL"){
      style.height = percentFilled +"%";
    } else {
      style.width = percentFilled +"%";
    }

    return(
      <div className={"web-label-level " + (this.props.data.level.position).toLowerCase()}>
        <div style={style}/>
      </div>
    );
  }

  render() {

    let style = {
      position:"relative",
      ...(this.props.parentElementProps && this.props.parentElementProps.style || {}),
      ...this.props.style,
      ...this.getLabelStyles(),
  };

    return (
      <div {...this.props.parentElementProps} style={style} className={`widgets--widget`}>
        <div className="widgets--widget-label">
          <Dotdotdot clamp={1}>{this.props.data.label || 'No Widget Name'}</Dotdotdot>
          {this.props.tools}
        </div>

        {this.props.data.isShowLevelEnabled && this.renderLabelLevel() }

        { /* widget content */ }

        { this.renderRealDataLabel() }

        { /* end widget content */ }

        {this.props.settingsModal}
        {this.props.resizeHandler}
      </div>
    );
  }

}

LabelWidget.Settings = LabelWidgetSettings;

export default LabelWidget;
