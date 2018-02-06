import React from 'react';
import {Chart} from 'components';
// import Widget from '../Widget';
import {
  Icon
} from 'antd';
import PropTypes from 'prop-types';
import LinearWidgetSettings from './settings';
import './styles.less';
import {Map, fromJS, List} from 'immutable';
import moment from 'moment';
import Canvasjs from 'canvasjs';

import Dotdotdot from 'react-dotdotdot';

class LinearWidget extends React.Component {

  static propTypes = {

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

    data  : PropTypes.object,
    params: PropTypes.object,
    name  : PropTypes.string,

    deviceId: PropTypes.any,

    widgets: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.generateData = this.generateData.bind(this);
  }

  dataDefaultOptions = {
    type: 'line',
    markerType: 'none',
    lineThickness: 1,
  };

  defaultToolTip = {
    enabled:  true,
    shared: true,
    contentFormatter: (data) => {

      const getTooltipTemplate = ( legendsTemplateFn, titleTemplateFn, data) => {
        return `
            <div class="chart-tooltip">
              ${titleTemplateFn(data)}
              <div class="chart-tooltip-legends">
                ${legendsTemplateFn(data)}
              </div>
            </div>
            `;
      };

      const getLegendsTemplate = (data) => {

        const getLegendTemplate = (name, value, color) => {
          return (
            `<div class="chart-tooltip-legends-legend">
                <div class="chart-tooltip-legends-legend-circle" style="background: ${color}"></div>
                <div class="chart-tooltip-legends-legend-name">${name}:</div>
                <div class="chart-tooltip-legends-legend-value">${value}</div>
              </div>`
          );
        };

        const legends = [];

        data.forEach((item) => {
          legends.push(getLegendTemplate(
            item.name,
            item.y,
            item.color
          ));
        });

        return legends.join('');

      };

      const getTitleTemplate = (data) => {
        return `<div class="chart-tooltip-title">${data[0].x}</div>`;
      };

      // highlight point
      const series = data.entries[0].dataSeries;
      series.markerType = 'circle';

      const tooltipData = [];

      data.entries.forEach((entry) => {

        const getFormattedValue = (format, value) => {

          if(format) {
            if(value instanceof Date)
              return Canvasjs.formatDate(value, format);

            if(!isNaN(Number(value)))
              return Canvasjs.formatNumber(value, format);
          }

          return value;
        };

        tooltipData.push({
          x: getFormattedValue(entry.dataSeries.xValueFormatString, entry.dataPoint.x),
          y: getFormattedValue(entry.dataSeries.yValueFormatString, entry.dataPoint.y),
          name: entry.dataSeries.name,
          color: entry.dataSeries.lineColor,
        });
      });

      return getTooltipTemplate(getLegendsTemplate, getTitleTemplate, tooltipData);

    }
  };

  getMinMaxXFromLegendsList(data) {
    let min = new Date().getTime();
    let max = 0;

    if(data && data instanceof List && data.size) {

      data.forEach(item => {

        if (item.has('dataPoints') && item.get('dataPoints').size) {
          [min, max] = item.get('dataPoints').reduce(([min, max], dataPoint) => {

            let value = dataPoint.get('x');

            if (value) {
              let valueTimestamp = moment(value).format('x');

              return [
                valueTimestamp <= min ? valueTimestamp : min,
                valueTimestamp >= max ? valueTimestamp : max];

            } else {
              return [min, max];
            }
          }, [min, max]);

        }
      });
    }

    return [min,max];
  }

  getTimeFormatForRange([dateFrom = 0, dateTo = 0]) {

    dateFrom = moment(parseInt(dateFrom));
    dateTo = moment(parseInt(dateTo));

    if (dateTo.diff(dateFrom, 'hours') === 0) {
      return {
        tickFormat: 'hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm:ss TT',
        labelMaxWidth: 50,
      };
    } else if (dateTo.diff(dateFrom, 'days') === 0) {
      return {
        tickFormat: 'hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm TT',
        labelMaxWidth: 50,
      };
    } else if (dateTo.diff(dateFrom, 'days') >= 1 && dateTo.diff(dateFrom, 'days') <= 6) {
      return {
        tickFormat: 'DDD, hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm TT',
        labelMaxWidth: 60,
      };
    } else if (dateTo.diff(dateFrom, 'days') >= 7 && dateTo.diff(dateFrom, 'month') === 0) {
      return {
        tickFormat: 'DD MMM, hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm TT',
        labelMaxWidth: 100,
      };
    } else if (dateTo.diff(dateFrom, 'month') >= 1) {
      return {
        tickFormat: 'DD MMM, hh:mm TT',
        hoverFormat: 'DDD, DD MMM, hh:mm TT, YYYY',
        labelMaxWidth: 100,
      };
    }

    return {
      tickFormat: null,
      hoverFormat: 'DDD, DD MMM, hh:mm:ss TT'
    };

  }

  generateData(source, sourceIndex) {

    if(!source.has('dataStream') || !source.hasIn(['dataStream', 'pin']))
      return null;

    const pin = this.props.history.get(String(sourceIndex));

    if(!pin)
      return null;

    const dataPoints = pin.get('data').map((item) => {

      return fromJS({
        x: moment(Number(item.get('x'))).toDate(),
        y: item.get('y')
      });
    });

    let dataSource = fromJS({
      ...this.dataDefaultOptions,
      color: `#${source.get('color')}` || null,
      name: source.get('label') || null,
      dataPoints: dataPoints || [],
    });

    return dataSource;
  }

  renderRealDataChart() {

    if (!this.props.data.sources || !this.props.data.sources.length || !this.props.history || this.props.loading === undefined)
      return (<div className="bar-chart-widget-no-data">No Data</div>);

    if (this.props.loading)
      return (<Icon type="loading"/>);

    const sources = fromJS(this.props.data.sources);

    let dataSources = sources.map(this.generateData).filter((source) => source !== null);

    let formats = this.getTimeFormatForRange(
      this.getMinMaxXFromLegendsList(dataSources)
    );

    dataSources = dataSources.map(dataSource =>
      dataSource.set('xValueFormatString', formats.hoverFormat).set('yValueFormatString', '###,###,###,###')
    );

    const config = {
      axisX: {
        labelMaxWidth: formats.labelMaxWidth,
        labelWrap: true,
        labelAngle: 0,
        valueFormatString: formats.tickFormat,
      },
      toolTip: this.defaultToolTip,
      data: dataSources.toJS()
    };

    return this.renderChartByParams(config);
  }

  renderChartByParams(config) {

    const hasData = !!(config.data.reduce((acc, item) => {
      if(Array.isArray(item.dataPoints) && acc < item.dataPoints.length)
        return item.dataPoints.length;
      return acc;
    }, 0));


    if (hasData) {
      return (
        <div className="widgets--widget-container">
          <Chart name={this.props.name} config={config}/>
        </div>
      );
    } else {
      return (
        <div className="bar-chart-widget-no-data">No Data</div>
      );
    }
  }

  render() {

    return (
      <div {...this.props.parentElementProps} className={`widgets--widget`}>
        <div className="widgets--widget-label">
          <Dotdotdot clamp={1}>{this.props.data.label || 'No Widget Name'}</Dotdotdot>
          {this.props.tools}
        </div>

        { /* widget content */ }

        { this.renderRealDataChart() }

        { /* end widget content */ }

        {this.props.settingsModal}
        {this.props.resizeHandler}
      </div>
    );
  }

}

LinearWidget.Settings = LinearWidgetSettings;

export default LinearWidget;


/*
* 1) Get data for own PINS
* 2) Draw data for these own PINS
* 3) Display labels for these PINS
* 4) Fix DataStreams
* 5) Multiple sources support */
