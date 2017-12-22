import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {Map, fromJS} from 'immutable';
import {Icon} from 'antd';
import Dotdotdot from 'react-dotdotdot';
import {WIDGETS_LABEL_TEXT_ALIGNMENT} from 'services/Widgets';
import Canvasjs from 'canvasjs';
import './styles.less';

import LabelWidgetSettings from './settings';

@connect((state) => ({
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
}))
class LabelWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,
    params: PropTypes.object,

    editable: PropTypes.bool,

    fetchRealData: PropTypes.bool,

    isChartPreview: PropTypes.bool,

    fakeData: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number,
    ]),

    onWidgetDelete: PropTypes.func,

    widgets: PropTypes.instanceOf(Map),
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
    if(Number(cellSize) === 1)
      return 'widgets--widget-web-label--value-size-1';

    if(Number(cellSize) === 2)
      return 'widgets--widget-web-label--value-size-2';

    if(Number(cellSize) >= 3)
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
        { !isNoData && (
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

  renderFakeDataLabel() {

    return this.renderLabelByParams({
      value: undefined,
      suffix: this.props.data.valueSuffix,
      alignment: this.props.data.alignment
    });
  }

  generateData(source, sourceIndex) {
    if (!source.has('dataStream') || !source.hasIn(['dataStream', 'pin']))
      return null;

    const pin = this.props.widgets.getIn([
      String(this.props.params.id),
      String(this.props.data.id),
      String(sourceIndex)
    ]);

    if (!pin)
      return null;

    const lastPoint = pin.get('data').last();

    return lastPoint && lastPoint.get('y') || null;
  }

  renderRealDataLabel() {

    if (!this.props.data.sources || !this.props.data.sources.length)
      return (<div>No data</div>);

    if (!this.props.widgets.hasIn([String(this.props.params.id), 'loading']) || this.props.widgets.getIn([this.props.params.id, 'loading']))
      return (<Icon type="loading"/>);

    const sources = fromJS(this.props.data.sources);

    const dataSources = sources.map(this.generateData).filter((source) => source !== null);

    return this.renderLabelByParams({
      value: dataSources.get(0) || null,
      suffix: this.props.data.valueSuffix,
      alignment: this.props.data.alignment
    });
  }

  renderLabelByData() {
    return this.renderLabelByParams({
      value: this.props.fakeData,
      suffix: this.props.data.valueSuffix,
      alignment: this.props.data.alignment,
    });
  }

  render() {
    if (this.props.fetchRealData)
      return this.renderRealDataLabel();

    if(this.props.isChartPreview)
      return this.renderLabelByData();

    return this.renderFakeDataLabel();
  }

}

LabelWidget.Settings = LabelWidgetSettings;

export default LabelWidget;
