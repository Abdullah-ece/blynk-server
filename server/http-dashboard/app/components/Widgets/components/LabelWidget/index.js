import React from 'react';
import PropTypes from 'prop-types';
import Dotdotdot from 'react-dotdotdot';
import { WIDGETS_LABEL_TEXT_ALIGNMENT } from 'services/Widgets';
import Canvasjs from 'canvasjs';
import './styles.less';

import LabelWidgetSettings from './settings';

/*

why use widget data wrapper:
rerender only component LabelWidget
pass data to it based on

*/

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

    value: PropTypes.string,

    parentElementProps: PropTypes.shape({
      id: PropTypes.string,
      onMouseUp: PropTypes.func,
      onTouchEnd: PropTypes.func,
      onMouseDown: PropTypes.func,
      style: PropTypes.object,
    }),

    tools: PropTypes.element,
    settingsModal: PropTypes.element,
    resizeHandler: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.element),
      PropTypes.element,
    ]),

  };

  constructor(props) {
    super(props);

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

    if (!isNaN(Number(value)) && Number(value) === 0)
      return 0;

    let finalFormat = '';
    let decimalLength = 0;

    if (this.props.data.decimalFormat.indexOf('#.#') >= 0) {
      let parts = this.props.data.decimalFormat.split('.');
      finalFormat += parts[0] + '0.';
      finalFormat += parts[1];
      decimalLength = parts[1].length;
    } else if (this.props.data.decimalFormat.indexOf('.') < 0) {
      let result = Canvasjs.formatNumber(value, finalFormat);
      return result.split('.')[0];
    }

    let result = Canvasjs.formatNumber(value, finalFormat);
    let resultParts = result.split('.');
    let resultDecimalLength = resultParts.length < 2 ? 0 : resultParts[1].length;
    result = resultDecimalLength == 0 ? result += '.' : result;

    if (decimalLength != resultDecimalLength) {
      for (let i = 0; i < decimalLength - resultDecimalLength; i++) {
        result += '0';
      }
    }

    if (result[result.length - 1] === '.') {
      result = result.substring(0, result.length - 1, 0);
    }

    return result;
  }

  renderLabelByParams(params = {
    alignment: WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT,
    value: null,
    suffix: null,
    customText: null
  }) {

    const alignmentClassName = this.getTextAlignmentClassNameByAlignment(params.alignment);

    const isNoData = params.value === null || params.value === undefined;

    const isStringValue = isNaN(Number(params.value));

    const valueClassName = this.getValueClassName(isStringValue);

    const valueSizeClassName = this.getValueSizeClassName(this.props.data.height);

    return (
      <div className={`widgets--widget-web-label ${alignmentClassName}`}>
        {!isNoData && (
          <div
            className={`widgets--widget-web-label--container ${valueSizeClassName}`}>
            <Dotdotdot clamp={1}>
              {!params.customText && (
                <span
                  className={`${valueClassName}`}>{isStringValue ? params.value : this.formatLabelValue(params.value)}</span>
              ) || (null)}
              {!params.customText && params.suffix && (
                <span
                  className="widgets--widget-web-label--suffix-label">{params.suffix || null}</span>
              )}
              {params.customText && (
                <span
                  className={"widgets--widget-web-label--custom-text"}>{params.customText || null}</span>
              )}
            </Dotdotdot>
          </div>
        ) || (
          <div
            className={`widgets--widget-web-label--container ${valueSizeClassName}`}>
            <span
              className={`widgets--widget-web-label--number-value`}>--</span>
          </div>
        )}
      </div>
    );
  }

  renderRealDataLabel() {

    const labelValue = this.getLabelValue();
    //
    // if (labelValue === null)
    //   return (<div className="bar-chart-widget-no-data">No Data</div>);

    return this.renderLabelByParams({
      value: labelValue,
      suffix: this.props.data.valueSuffix,
      alignment: this.props.data.alignment,
      customText: this.getCurrentColorSet().customText,
    });
  }

  getLabelValue() {
    return this.props.value;
  }

  getCurrentColorSet() {
    const labelValue = this.getLabelValue();
    let currentColorSet = null;
    // Choose color set from all color sets based on label value. If label value not fit to any of color sets then
    // select the first color set if value is lower then min value, select the last if value is higher then max, or select default
    if (this.props.data.colorsSet && labelValue !== null && labelValue !== undefined) {
      currentColorSet = (this.props.data.colorsSet.filter((obj) => (obj.min <= Number(labelValue) && obj.max >= Number(labelValue))))[0] || null;
      if (currentColorSet === null) {
        currentColorSet = labelValue < this.props.data.colorsSet[0].min ? this.props.data.colorsSet[0] :
          labelValue > this.props.data.colorsSet[this.props.data.colorsSet.length - 1].max ? this.props.data.colorsSet[this.props.data.colorsSet.length - 1] :
            { backgroundColor: "ffffff", textColor: "000000" };
      }
    } else {
      currentColorSet = { backgroundColor: "ffffff", textColor: "000000" };
    }

    return currentColorSet;
  }

  getLabelStyles() {

    return !this.props.data.isColorSetEnabled ? {
      backgroundColor: "#" + this.props.data.backgroundColor,
      color: "#" + this.props.data.textColor
    } : (({ backgroundColor, textColor }) => {
      return {
        backgroundColor: "#" + backgroundColor,
        color: "#" + textColor
      };
    })(this.getCurrentColorSet());
  }

  renderLabelLevel() {

    if (this.props.data.level.min >= this.props.data.level.max) {

      return null;
    }

    let percentFilled = 0;
    if (this.getLabelValue() !== null && this.getLabelValue() !== undefined) {
      percentFilled = Math.round((this.getLabelValue() - this.props.data.level.min) / ((this.props.data.level.max - this.props.data.level.min) / 100));
    }

    if (percentFilled < 0)
      percentFilled = 0;

    if (percentFilled > 100)
      percentFilled = 100;

    let style = {
      position: "absolute",
      bottom: 0,
      left: 0,
      backgroundColor: "#" + this.props.data.level.color,
      height: "100%",
      width: "100%",
    };

    if (this.props.data.level.position === "VERTICAL") {
      style.height = percentFilled + "%";
    } else {
      style.width = percentFilled + "%";
    }

    return (
      <div
        className={"web-label-level " + (this.props.data.level.position).toLowerCase()}>
        <div style={style}/>
      </div>
    );
  }

  getTextAlignStyle(alignment) {
    if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.LEFT)
      return 'left';

    if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.CENTER)
      return 'center';

    if (alignment === WIDGETS_LABEL_TEXT_ALIGNMENT.RIGHT)
      return 'right';
  }


  render() {

    let style = {
      position: "relative",
      ...(this.props.parentElementProps && this.props.parentElementProps.style || {}),
      ...this.props.style,
      ...this.getLabelStyles(),
      textAlign: this.getTextAlignStyle(this.props.data.alignment),
    };
    const labelColor = (this.props.data.textColor === "DEFAULT" && !this.props.data.isColorSetEnabled) ? "#58595d" : null;
    return (
      <div {...this.props.parentElementProps} style={style}
           className={`widgets--widget`}>
        <div className="widgets--widget-label" style={{ color: labelColor }}>
          <Dotdotdot
            clamp={1}>{this.props.data.label || 'No Widget Name'}</Dotdotdot>
          {this.props.tools}
        </div>

        {this.props.data.isShowLevelEnabled && this.renderLabelLevel()}

        { /* widget content */}

        {this.renderRealDataLabel()}

        { /* end widget content */}

        {this.props.settingsModal}
        {this.props.resizeHandler}
      </div>
    );
  }

}

LabelWidget.Settings = LabelWidgetSettings;

export default LabelWidget;
