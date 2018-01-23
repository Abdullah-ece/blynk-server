import React from 'react';
import {Chart} from 'components';
// import Widget from '../Widget';
import {
  Icon
} from 'antd';
import PropTypes from 'prop-types';
import LinearWidgetSettings from './settings';
import './styles.less';
import {connect} from 'react-redux';
import {Map, fromJS, List} from 'immutable';
import moment from 'moment';
import _ from 'lodash';
import Canvasjs from 'canvasjs';

@connect((state) => ({
  widgets: state.Widgets && state.Widgets.get('widgetsData'),
}))
class LinearWidget extends React.Component {

  static propTypes = {
    data: PropTypes.object,
    params: PropTypes.object,
    name: PropTypes.string,

    deviceId: PropTypes.any,

    editable: PropTypes.bool,

    previewMode: PropTypes.bool,

    fetchRealData: PropTypes.bool,

    onWidgetDelete: PropTypes.func,

    deviceId: PropTypes.number,

    widgets: PropTypes.instanceOf(Map),
  };

  constructor(props) {
    super(props);

    this.generateData = this.generateData.bind(this);
    this.generateFakeData = this.generateFakeData.bind(this);
  }

  dataDefaultOptions = {
    type: 'line',
    markerType: 'none',
    lineThickness: 1,
  };

  defaultToolTip = {
    enabled:  !(this.props.editable && !this.props.previewMode),
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

  generateFakeData(source) {
    let dataSource = fromJS({
      ...this.dataDefaultOptions,
      color: `#${source.get('color')}` || null,
      name: source.get('label') || null,
      dataPoints: [],
      xValueFormatString: 'DD-MMMs',
      yValueFormatString: '#,###,##',
    });

    for(let i = 0; i < 7; i++) {
      dataSource = dataSource.update('dataPoints', (dataPoints) => dataPoints.push(fromJS(
        { x: moment().subtract(i, 'days').toDate(), y: _.random(0,500)}
      )));
    }

    return dataSource;
  }

  renderFakeDataChart() {

    const MAX_LABEL_LENGTH = 60;
    const DEFAULT_TICK_FORMAT = 'DDD, hh:mm TT';
    const DEFAULT_HOVER_FORMAT = 'DDD, DD MMM, hh:mm TT';

    const data = fromJS(this.props.data);

    if(!data.has('sources') || !data.get('sources').size)
      return null;

    const dataSources = data.get('sources')
      .map(this.generateFakeData)
      .map(dataSource =>
        dataSource.set('xValueFormatString', DEFAULT_HOVER_FORMAT).set('yValueFormatString', '###,###,###,###')
      );

    const config = {
      axisX: {
        labelMaxWidth: MAX_LABEL_LENGTH,
        labelAngle: 0,
        valueFormatString: DEFAULT_TICK_FORMAT,
      },
      toolTip: this.defaultToolTip,
      data: dataSources.toJS()
    };

    return this.renderChartByParams(config);
  }

  generateData(source, sourceIndex) {

    if(!source.has('dataStream') || !source.hasIn(['dataStream', 'pin']))
      return null;

    const pin = this.props.widgets.getIn([
      String(this.props.deviceId),
      String(this.props.data.id),
      String(sourceIndex)
    ]);

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

    if (!this.props.data.sources || !this.props.data.sources.length)
      return (<div>No data</div>);

    if (!this.props.widgets.hasIn([String(this.props.deviceId), 'loading']) || this.props.widgets.getIn([this.props.deviceId, 'loading']))
      return (<Icon type="loading" />);

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
    if (!this.props.fetchRealData)
      return this.renderFakeDataChart();

    return this.renderRealDataChart();
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
