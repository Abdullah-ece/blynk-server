import React from 'react';
import Canvasjs from 'canvasjs';
import PropTypes from 'prop-types';
import _ from 'lodash';
import './styles.less';

class Chart extends React.Component {

  static propTypes = {
    config: PropTypes.shape({
      data: PropTypes.array,
      axisY: PropTypes.object,
      axisX: PropTypes.object,
      legend: PropTypes.object,
    }),
    colorSets: PropTypes.array,
    className: PropTypes.string,
  };

  componentDidMount() {

    const chartConfig = _.merge(this.chartDefaultOptions, this.props.config || {});

    if(this.props.colorSets && typeof this.props.colorSets === 'object' && this.props.colorSets.length !== 'undefined')
    this.props.colorSets.map((colorSet) => {
      Canvasjs.addColorSet(colorSet.name, colorSet.colors);
    });

    this.chart = new Canvasjs.Chart(this.chartRef, chartConfig);

    this.chart.render();

  }

  componentDidUpdate() {

    const chartConfig = _.merge(this.chartDefaultOptions, this.props.config || {});

    this.chart = new Canvasjs.Chart(this.chartRef, chartConfig);

    this.chart.render();
  }

  legendDefaultOptions = {
    fontFamily: 'PF DinDisplay Pro',
    fontWeight: 300,
    fontColor: 'rgba(33, 34, 39, 0.75)',
    fontSize: 12,

    verticalAlign: 'top',
    horizontalAlign: 'left',
    markerMargin: 8,
  };

  axisYDefaultOptions = {
    gridColor: 'rgba(0,0,0,0)',
    gridDashType: 'none',
    labelFontFamily: 'PF DinDisplay Pro',
    labelFontWeight: 300,
    labelFontColor: 'rgba(33, 34, 39, 0.75)',
    labelFontSize: 12,
    includeZero: false,
    tickLength: 2,
    tickColor: '#fff',
    margin: 0,
    lineColor: 'rgba(33, 34, 39, 0.25)',
    lineThickness: 1,
  };

  axisXDefaultOptions = {
    gridColor: 'rgba(33, 34, 39, 0.05)',
    gridThickness: 1,
    labelFontSize: 10,
    labelFontFamily: 'PF DinDisplay Pro',
    labelFontWeight: 300,
    labelFontColor: 'rgba(33, 34, 39, 0.75)',
    tickLength: 12,
    tickColor: '#fff',
    margin: 12,
    lineColor: 'rgba(33, 34, 39, 0.25)'
  };

  chartDefaultOptions = {
    animationEnabled: true,
    axisY:{
      includeZero: false,
      ...this.axisYDefaultOptions
    },
    axisX: {
      ...this.axisXDefaultOptions,
    },
    data: [],
    legend: {
      ...this.legendDefaultOptions,
    },
    toolTip: {
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
    }
  };

  render() {

    return (
      <div className={this.props.className}>
        <div className="canvasjs-widget-container" ref={(ref) => this.chartRef = ref}/>
      </div>
    );
  }

}

export default Chart;
