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

    if(this.props.colorSets.constructor === Array)
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
